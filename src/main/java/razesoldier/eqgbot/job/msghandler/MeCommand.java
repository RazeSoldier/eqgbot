/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.job.msghandler;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import razesoldier.eqgbot.EVEUser;
import razesoldier.eqgbot.dba.SQLExecuteException;

class MeCommand implements MessageHandler {
    private final GroupMessageEvent event;

    public MeCommand(GroupMessageEvent event) {
        this.event = event;
    }

    @Override
    public void handle() throws SQLExecuteException {
        final var sender = event.getSender();
        var user = EVEUser.newInstanceFromOF(sender.getId());
        String text;
        if (user != null) {
            text = user.getCorpName() + '-' + user.getName();
        } else {
            text = "没有绑定ESI哦或者根本没有注册EVE？？？";
        }

        event.getGroup().sendMessage(
                new At(sender.getId()).plus(text)
        );
    }
}
