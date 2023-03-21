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
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.Config;
import razesoldier.eqgbot.yy.*;

import java.util.Optional;
import java.util.regex.Pattern;


/**
 * 通过群命令对指定YY用户赋予黄马或者移除黄马权限
 */
@Log
class ManageYYAdmin extends FeatureBase {
    private final Bot bot;
    private final Long managedGroup;
    private final Config.YYLoginCredential yyLoginCredential;

    public ManageYYAdmin(Bot bot, Long managedGroup, Config.YYLoginCredential yyLoginCredential) {
        this.bot = bot;
        this.managedGroup = managedGroup;
        this.yyLoginCredential = yyLoginCredential;
    }

    @Override
    void handle() {
        YYClient yyClient;
        try {
            yyClient = new YYClient(yyLoginCredential);
            yyClient.attemptLogin();
        } catch (LoginException e) {
            log.warning("Login YY failed");
            return;
        }

        bot.getEventChannel()
                .filterIsInstance(GroupMessageEvent.class)
                .filter(event -> event.getGroup().getId() == managedGroup)
                .filter(event -> event.getSender().getPermission().compareTo(MemberPermission.ADMINISTRATOR) >= 0) // 只允许管理员和群主使用本命令
                .subscribeAlways(GroupMessageEvent.class, event -> {
                    var msg = event.getMessage().contentToString();
                    try {
                        dispatch(msg, yyClient).ifPresent(yyOperationResult -> event.getGroup().sendMessage(String.valueOf(yyOperationResult)));
                    } catch (YYOperationException e) {
                        log.warning(e.getMessage());
                        event.getGroup().sendMessage("出错");
                    }
                });
    }

    private Optional<YYOperationResult> dispatch(String msg, YYClient yyClient) throws YYOperationException {
        var assignRoleOpResult = tryAssignRole(msg, yyClient);
        if (assignRoleOpResult.isPresent()) {
            return assignRoleOpResult;
        }
        return tryRevokeRole(msg, yyClient);
    }

    private Optional<YYOperationResult> tryAssignRole(@NotNull String str, YYClient yyClient) throws YYOperationException {
        var longOptional = matchAssignRole(str);
        if (longOptional.isPresent()) {
            var accountOptional = yyClient.getAccountByYYId(longOptional.get());
            if (accountOptional.isPresent()) {
                return Optional.of(new AssignAdminAction(yyClient).handle(accountOptional.get()));
            }
        }
        return Optional.empty();
    }

    private Optional<YYOperationResult> tryRevokeRole(String msg, YYClient yyClient) throws YYOperationException {
        var longOptional = matchRevokeRole(msg);
        if (longOptional.isPresent()) {
            var accountOptional = yyClient.getAccountByYYId(longOptional.get());
            if (accountOptional.isPresent()) {
                return Optional.of(new RevokeAdminAction(yyClient).handle(accountOptional.get()));
            }
        }
        return Optional.empty();
    }

    private Optional<Long> matchAssignRole(@NotNull String str) {
        var pattern = Pattern.compile("^发黄马\\s(\\d*)$");
        var matcher = pattern.matcher(str);
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1)).describeConstable();
        } else {
            return Optional.empty();
        }
    }

    private Optional<Long> matchRevokeRole(@NotNull String str) {
        var pattern = Pattern.compile("^下黄马\\s(\\d*)$");
        var matcher = pattern.matcher(str);
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1)).describeConstable();
        } else {
            return Optional.empty();
        }
    }
}
