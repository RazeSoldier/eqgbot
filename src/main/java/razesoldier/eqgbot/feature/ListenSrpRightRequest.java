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
import net.mamoe.mirai.utils.MiraiLogger;
import razesoldier.eqgbot.job.HandleSrpRightRequest;

class ListenSrpRightRequest extends FeatureBase {
    private final Bot bot;
    private final MiraiLogger logger;

    ListenSrpRightRequest(Bot bot, MiraiLogger logger) {
        this.bot = bot;
        this.logger = logger;
    }

    @Override
    void handle() {
        Events.registerEvents(bot, new HandleSrpRightRequest(logger));
    }
}
