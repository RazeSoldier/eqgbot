/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.feature;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Member;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.CharacterFilter;
import razesoldier.eqgbot.EVEUser;
import razesoldier.eqgbot.dba.SQLExecuteException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ExportInvalidPingGroupMembers extends FeatureBase {
    private final Bot bot;
    private final Integer pingGroupNumber;

    ExportInvalidPingGroupMembers(Bot bot, Integer pingGroupNumber) {
        this.bot = bot;
        this.pingGroupNumber = pingGroupNumber;
    }

    @Override
    void handle() {
        var members = bot.getGroupOrFail(pingGroupNumber).getMembers();
        List<Member> invalidMembers = new ArrayList<>();
        // 遍历群成员列表，检查EVEUser.newInstanceFromGF()的返回值来判断该成员是否应该在这个群
        members.forEach(member -> {
            try {
                Optional<List<EVEUser>> users = EVEUser.newInstance(member.getId());
                if (users.isPresent()) {
                    if (CharacterFilter.of(users.get()).filterAlliance(562593865).isEmpty()) {
                        invalidMembers.add(member);
                    }
                } else {
                    invalidMembers.add(member);
                }
            } catch (SQLExecuteException e) {
                e.printStackTrace();
            }
        });

        export(invalidMembers);
    }

    private void export(@NotNull List<Member> invalidMembers) {
        StringBuilder stringBuilder = new StringBuilder();
        invalidMembers.forEach(member -> stringBuilder.append(member.getId()).append("\n"));
        try {
            Files.writeString(Path.of(System.getProperty("user.dir") + "/invalid_members.txt"), stringBuilder.toString());
        } catch (IOException e) {
            System.out.println("Failed to export");
            e.printStackTrace();
        }
    }
}
