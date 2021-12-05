/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.queue;

import redis.clients.jedis.Jedis;

import java.util.Optional;

public class RedisMessageQueue implements MessageQueue {
    private final String queueKey;
    private final Jedis jedis;

    RedisMessageQueue(String queueKey, String password) {
        this.queueKey = queueKey;
        jedis = new Jedis();
        jedis.auth(password);
    }

    @Override
    public Optional<String> getMessage() {
        String msg = jedis.lpop(queueKey);
        if (msg == null) {
            return Optional.empty();
        }
        return Optional.of(msg);
    }
}
