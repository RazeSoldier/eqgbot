/*
 * Copyright 2019-2022 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.feature;

import io.timeandspace.cronscheduler.CronScheduler;
import io.timeandspace.cronscheduler.CronTask;
import net.mamoe.mirai.Bot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.job.HandleMessageQueue;
import razesoldier.eqgbot.queue.MessageQueue;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StructureAlert extends FeatureBase {
    private final Bot bot;
    private final List<Long> groups;
    private final MessageQueue messageQueue;

    StructureAlert(Bot bot, List<Long> groups, MessageQueue messageQueue) {
        this.bot = bot;
        this.groups = groups;
        this.messageQueue = messageQueue;
    }

    @Override
    void handle() {
        CronScheduler scheduler = CronScheduler.create(Duration.ofMinutes(5));
        // 每分钟执行一次
        scheduler.scheduleAtFixedRateSkippingToLatest(0, 1, TimeUnit.MINUTES, getTask());
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    private CronTask getTask() {
        return new HandleMessageQueue(messageQueue, groups.stream().map(bot::getGroupOrFail).toList());
    }
}
