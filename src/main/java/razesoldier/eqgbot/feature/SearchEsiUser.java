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
import net.mamoe.mirai.event.events.GroupMessageEvent;
import razesoldier.eqgbot.job.HandleSearchEsiUserCommand;

public class SearchEsiUser extends FeatureBase {
    private final Bot bot;

    public SearchEsiUser(Bot bot) {
        this.bot = bot;
    }

    @Override
    void handle() {
        bot.getEventChannel()
                .filterIsInstance(GroupMessageEvent.class)
                .filter(event -> event.getGroup().getId() == 133052897 || event.getGroup().getId() == 636038081)
                .registerListenerHost(new HandleSearchEsiUserCommand());
    }
}
