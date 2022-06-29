/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.feature;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import razesoldier.eqgbot.job.HandleJoinGroupEvent;

import java.util.List;

class ListenJoinGroupRequest extends FeatureBase {
    private final Bot bot;
    private final List<Long> vettedGroupList;
    private final List<Long> pingGroupList;
    private final MiraiLogger logger;

    public ListenJoinGroupRequest(Bot bot, List<Long> vettedGroupList, List<Long> pingGroupList, MiraiLogger logger) {
        this.bot = bot;
        this.vettedGroupList = vettedGroupList;
        this.pingGroupList = pingGroupList;
        this.logger = logger;
    }

    @Override
    void handle() {
        bot.getEventChannel()
                .filterIsInstance(MemberJoinRequestEvent.class)
                .filter(event -> vettedGroupList.contains(event.getGroupId()))
                .subscribeAlways(MemberJoinRequestEvent.class, new HandleJoinGroupEvent(vettedGroupList, pingGroupList, logger, bot));
    }
}
