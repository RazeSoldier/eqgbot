/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.feature;

import net.mamoe.mirai.Bot;
import razesoldier.eqgbot.job.HandleGroupMessageEvent;

import java.util.List;

class ListenGroupMessage extends FeatureBase {
    private final Bot bot;

    ListenGroupMessage(Bot bot) {
        this.bot = bot;
    }

    @Override
    void handle() {
        bot.getEventChannel().registerListenerHost(new HandleGroupMessageEvent(List.of((long)1043885399, (long)906952212, (long)133052897)));
    }
}
