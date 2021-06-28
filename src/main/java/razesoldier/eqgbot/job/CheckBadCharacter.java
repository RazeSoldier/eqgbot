/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.job;

import io.timeandspace.cronscheduler.CronTask;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.GameServer;
import razesoldier.eqgbot.dba.DatabaseAccessHolding;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用来检查联盟红的当前雇佣情况。如果在3V或者MSN内则通知
 */
public class CheckBadCharacter implements Job, CronTask {
    @NotNull
    private final Bot bot;
    @NotNull
    private final MiraiLogger logger;
    @NotNull
    private final Integer noticeGroup;

    public CheckBadCharacter(@NotNull Bot bot, @NotNull MiraiLogger logger, @NotNull Integer noticeGroup) {
        this.bot = bot;
        this.logger = logger;
        this.noticeGroup = noticeGroup;
    }

    @Override
    public void run(long scheduledRunTimeMillis) {
        try (var conn = DatabaseAccessHolding.getInstance().getConnection(GameServer.OF)) {
            List<BadCharacter> characterList = new ArrayList<>();
            // 查询当前的联盟红的角色列表
            {
                var sql = """
                        select alliance_contacts.contact_id, standing, ca.alliance_id, ca.corporation_id, un.name
                        from alliance_contacts
                                 inner join character_affiliations ca on ca.character_id = alliance_contacts.contact_id
                                 inner join universe_names un on un.entity_id = alliance_contacts.contact_id
                        where alliance_contacts.alliance_id = 99009310
                          and standing <= -5
                          and contact_type = 'character'""";
                var res = DatabaseAccessHolding.executeQuery(conn, sql);
                while (res.next()) {
                    BadCharacter c = new BadCharacter();
                    c.id = res.getInt("contact_id");
                    c.name = res.getString("name");
                    c.standing = res.getInt("standing");
                    c.corpId = res.getInt("corporation_id");
                    c.allianceId = res.getInt("alliance_id");
                    characterList.add(c);
                }
            }
            {
                characterList.forEach(c -> {
                    // 判断联盟红是否在联盟内
                    if (c.allianceId == 99009310 || c.allianceId == 99009275) {
                        sendMessage(c);
                    }
                });
            }
        } catch (SQLException e) {
            logger.warning(e);
        }
    }

    private void sendMessage(@NotNull BadCharacter badCharacter) {
        var msg = "==警告==\n" + badCharacter.name + " 已经收入联盟内";
        bot.getGroupOrFail(noticeGroup).sendMessage(msg);
    }

    private static class BadCharacter {
        String name;
        Integer standing;
        Integer id;
        Integer corpId;
        Integer allianceId;
    }
}
