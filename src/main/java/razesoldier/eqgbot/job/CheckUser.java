/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
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
import org.apache.commons.lang3.StringUtils;
import razesoldier.eqgbot.GameServer;
import razesoldier.eqgbot.QQWhiteList;
import razesoldier.eqgbot.dba.DatabaseAccessHolding;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 检查给定QQ群内的成员是否合法。并且发送一个名单到指定QQ号。
 */
public abstract class CheckUser implements Job, CronTask {
    private final Bot bot;
    private final MiraiLogger logger;
    private final long groupId;
    private final long recipientId;

    public CheckUser(Bot bot, MiraiLogger logger, long groupId, long recipientId) {
        this.bot = bot;
        this.logger = logger;
        this.groupId = groupId;
        this.recipientId = recipientId;
    }

    @Override
    public void run(long scheduledRunTimeMillis) {
        try {
            List<Long> qqList = new ArrayList<>();
            try (Connection connection = DatabaseAccessHolding.getInstance().getConnection(getGameServer())) {
                var set = DatabaseAccessHolding.executeQuery(
                        connection, getQuerySQl()
                );
                while (set.next()) {
                    each(qqList, set);
                }
            }

            List<Long> kickList = new ArrayList<>();
            bot.getGroup(groupId).getMembers().forEach(member -> {
                final var id = member.getId();
                if (!qqList.contains(id)) {
                    if (QQWhiteList.getInstance().has(id)) {
                        return;
                    }
                    kickList.add(id);
                }
            });

            if (kickList.isEmpty()) {
                return;
            }
            bot.getFriend(recipientId).sendMessage(getGameServer().toString() + "\n"
                    + StringUtils.join(kickList, "\n"));
        } catch (Exception throwables) {
            logger.info(throwables.getMessage());
        }
    }

    abstract protected String getQuerySQl();
    abstract protected void each(final List<Long> qqList, ResultSet resultSet) throws SQLException;
    abstract protected GameServer getGameServer();
}
