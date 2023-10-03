/*
 * Copyright 2019-2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.feature;

import lombok.extern.java.Log;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.ExternalResource;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.EVECharacter;
import razesoldier.eqgbot.EVEUser;
import razesoldier.eqgbot.dba.SQLExecuteException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用来监听管理群的命令
 */
@Log
class AdminCommandListener extends FeatureBase {
    private final Bot bot;
    private final Long group;

    AdminCommandListener(Bot bot, Long group) {
        this.bot = bot;
        this.group = group;
    }

    @Override
    void handle() {
        bot.getEventChannel()
                .filterIsInstance(GroupMessageEvent.class)
                .filter(event -> event.getGroup().getId() == group)
                .subscribeAlways(GroupMessageEvent.class, this::handleMessage);
    }

    private void handleMessage(@NotNull GroupMessageEvent event) {
        var message = event.getMessage().contentToString();
        if (message.equals(".help")) {
            printHelp(event.getGroup());
            return;
        }
        if (message.startsWith(".check_group ")) {
            log.info("Call AdminCommandListener::checkGroup()");
            checkGroup(event);
        }
    }

    private void checkGroup(@NotNull GroupMessageEvent event) {
        String msg = event.getMessage().contentToString();
        Group currentGroup = event.getGroup();
        Pattern pattern = Pattern.compile("^\\.check_group\\s(\\d*)$");
        Matcher matcher = pattern.matcher(msg);
        if (!matcher.find()) {
            currentGroup.sendMessage("错误的用法\n.check_group <QQ群号> 检查指定QQ群的ESI注册情况");
            return;
        }
        String groupNumber = matcher.group(1);
        Group targetGroup = bot.getGroup(Long.parseLong(groupNumber));
        if (targetGroup == null) {
            currentGroup.sendMessage("机器人不在此群");
            return;
        }

        try {
            log.info("Start check group: " + targetGroup.getId());
            CSVExporter csvExporter = new GroupEsiChecker(targetGroup).handle();
            log.info("Check finished");
            var filename = "check_group_result-" + Instant.now().getEpochSecond() + ".csv";
            Path path = Path.of(filename);
            log.info("Write file to " + filename);
            Files.writeString(path, csvExporter.toString());
            try (ExternalResource externalResource = ExternalResource.create(path.toFile())) {
                log.info("Upload file to group " + currentGroup.getId());
                currentGroup.getFiles().uploadNewFile(filename, externalResource);
            }
            log.info("Delete file " + filename);
            Files.delete(path);
        } catch (SQLExecuteException|IOException e) {
            currentGroup.sendMessage(String.format("[SQLExecuteException] %s", e.getMessage()));
        }
    }

    private void printHelp(@NotNull Group group) {
        group.sendMessage(".check_group <QQ群号> 检查指定QQ群的ESI注册情况");
    }

    /**
     * 用来检查QQ群的ESI注册情况
     */
    private static class GroupEsiChecker {
        private final Group group;
        private final CSVExporter csvExporter = new CSVExporter();

        GroupEsiChecker(Group group) {
            this.group = group;
        }

        CSVExporter handle() throws SQLExecuteException {
            ContactList<NormalMember> members = group.getMembers();
            for (var member : members) {
                Optional<List<EVEUser>> eveUsers = EVEUser.newInstance(member.getId());
                if (eveUsers.isPresent()) {
                    handleEveUsers(eveUsers.get(), member.getId());
                } else {
                    handleNoEsiUser(member.getId());
                }
            }
            return csvExporter;
        }

        private void handleEveUsers(@NotNull List<EVEUser> eveUsers, long qq) throws SQLExecuteException {
            for (EVEUser eveUser : eveUsers) {
                EVECharacter mainCharacter = eveUser.getMainCharacter();
                var allianceName = mainCharacter.getAllianceName() != null ? mainCharacter.getAllianceName() : ""; // 预处理防止NPE
                csvExporter.addRow(List.of(String.valueOf(qq), mainCharacter.getName(), mainCharacter.getCorporationName(), allianceName));
            }
        }

        private void handleNoEsiUser(long qq) {
            csvExporter.addRow(List.of(String.valueOf(qq)));
        }
    }

    private static class CSVExporter {
        private final List<List<String>> data = new ArrayList<>();

        void addRow(List<String> row) {
            data.add(row);
        }

        @Override
        @NotNull
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (List<String> row : data) {
                for (String cell : row) {
                    stringBuilder.append(cell).append(",");
                }
                stringBuilder.append("\n");
            }

            return stringBuilder.toString();
        }
    }
}
