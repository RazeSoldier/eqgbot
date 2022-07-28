/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.job;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;

public class HandleDMRelay {
    private final Bot bot;
    private final Long src;
    private final Long dest;

    public HandleDMRelay(Bot bot, Long src, Long dest) {
        this.bot = bot;
        this.src = src;
        this.dest = dest;
    }

    public void subscribe() {
        GlobalEventChannel.INSTANCE
                .filterIsInstance(FriendMessageEvent.class)
                .filter(event -> event.getSender().getId() == src)
                .subscribeAlways(FriendMessageEvent.class, event -> bot.getGroupOrFail(dest).sendMessage(event.getMessage().contentToString()));
    }
}
