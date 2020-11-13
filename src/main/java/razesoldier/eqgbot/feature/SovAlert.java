/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
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
import razesoldier.eqgbot.job.CheckCharacterNotification;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 国服主权预警。
 */
class SovAlert extends FeatureBase {
    private final Bot bot;
    private final MiraiLogger logger;
    private final int groupId;

    SovAlert(Bot bot, MiraiLogger logger, int groupId) {
        this.bot = bot;
        this.logger = logger;
        this.groupId = groupId;
    }

    @Override
    void handle() {
        CronScheduler scheduler = CronScheduler.create(Duration.ofMinutes(5));
        scheduler.scheduleAtFixedRateSkippingToLatest(0, 2, TimeUnit.MINUTES,
                new CheckCharacterNotification(bot, logger, groupId));
    }
}
