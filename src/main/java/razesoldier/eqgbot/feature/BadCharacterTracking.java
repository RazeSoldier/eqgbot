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
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.job.CheckBadCharacter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 用来监视联盟红的当前雇佣情况。如果在3V或者MSN内则通知。每小时检查一次。
 */
class BadCharacterTracking extends FeatureBase {
    @NotNull
    private final Bot bot;
    @NotNull
    private final MiraiLogger logger;
    @NotNull
    private final Integer noticeGroup;

    BadCharacterTracking(@NotNull Bot bot, @NotNull MiraiLogger logger, @NotNull Integer noticeGroup) {
        this.bot = bot;
        this.logger = logger;
        this.noticeGroup = noticeGroup;
    }

    @Override
    void handle() {
        CronScheduler scheduler = CronScheduler.create(Duration.ofMinutes(5));
        scheduler.scheduleAtFixedRateSkippingToLatest(0, 1, TimeUnit.HOURS,
                new CheckBadCharacter(bot, logger, noticeGroup));
    }
}
