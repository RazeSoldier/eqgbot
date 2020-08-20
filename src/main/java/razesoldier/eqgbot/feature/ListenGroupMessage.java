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
import net.mamoe.mirai.event.Events;
import razesoldier.eqgbot.job.HandleGroupMessageEvent;

class ListenGroupMessage extends FeatureBase {
    private final Bot bot;

    ListenGroupMessage(Bot bot) {
        this.bot = bot;
    }

    @Override
    void handle() {
        Events.registerEvents(bot, new HandleGroupMessageEvent(1043885399));
    }
}
