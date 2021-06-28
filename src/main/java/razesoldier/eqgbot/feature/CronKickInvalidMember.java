/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.feature;

import io.timeandspace.cronscheduler.CronScheduler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.MiraiLogger;
import razesoldier.eqgbot.GameServer;
import razesoldier.eqgbot.job.CheckUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

class CronKickInvalidMember extends FeatureBase {
    private final Bot bot;
    private final MiraiLogger logger;

    CronKickInvalidMember(Bot bot, MiraiLogger logger) {
        this.bot = bot;
        this.logger = logger;
    }

    @Override
    void handle() {
        CronScheduler scheduler = CronScheduler.create(Duration.ofMinutes(5));
        scheduler.scheduleAtFixedRateSkippingToLatest(
                0, 1, TimeUnit.DAYS, new CheckUser(bot, logger, 920169144, 1509251138) {
                    @Override
                    protected String getQuerySQl() {
                        return "select qqs.qq_id,characters.alliance_id from users,qqs,characters " +
                                "where qqs.user_id=users.id and users.id=characters.id and characters.alliance_id=562593865";
                    }

                    @Override
                    protected void each(List<Long> qqList, ResultSet resultSet) throws SQLException {
                        qqList.add(resultSet.getLong("qq_id"));
                    }

                    @Override
                    protected GameServer getGameServer() {
                        return GameServer.GF;
                    }
                }
        );
        scheduler.scheduleAtFixedRateSkippingToLatest(
                0, 1, TimeUnit.DAYS, new CheckUser(bot, logger, 876472453, 1509251138) {
                    @Override
                    protected String getQuerySQl() {
                        return "select qq from qq";
                    }

                    @Override
                    protected void each(List<Long> qqList, ResultSet resultSet) throws SQLException {
                        try {
                            qqList.add(Long.valueOf(resultSet.getString("qq")));
                        } catch (NumberFormatException e) {
                            logger.info(e.getMessage());
                        }
                    }

                    @Override
                    protected GameServer getGameServer() {
                        return GameServer.OF;
                    }
                }
        );
    }
}
