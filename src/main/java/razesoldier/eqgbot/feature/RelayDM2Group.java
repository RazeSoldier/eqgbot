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
import razesoldier.eqgbot.Config;
import razesoldier.eqgbot.job.HandleDMRelay;

import java.util.List;

/**
 * 转发私聊的消息到指定群
 */
public class RelayDM2Group extends FeatureBase {
    private final Bot bot;
    private final List<Config.DMRelayMap> dmRelayList;

    public RelayDM2Group(Bot bot, List<Config.DMRelayMap> dmRelayList) {
        this.bot = bot;
        this.dmRelayList = dmRelayList;
    }

    @Override
    void handle() {
        for (Config.DMRelayMap map : dmRelayList) {
            new HandleDMRelay(bot, map.getSrc(), map.getDest()).subscribe();
        }
    }
}
