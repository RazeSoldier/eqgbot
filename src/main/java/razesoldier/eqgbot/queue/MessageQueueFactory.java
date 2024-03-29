/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.queue;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.Config;

public class MessageQueueFactory {
    private MessageQueueFactory() {}

    @NotNull
    @Contract("_ -> new")
    public static MessageQueue newSovAlertQueue(@NotNull Config config) {
        return new RedisMessageQueue(config.getSovAlertQueueKey(), config.getRedisPassword());
    }

    @NotNull
    @Contract("_ -> new")
    public static MessageQueue newStructureAlertQueue(@NotNull Config config) {
        return new RedisMessageQueue(config.getStructureAlertQueueKey(), config.getRedisPassword());
    }
}
