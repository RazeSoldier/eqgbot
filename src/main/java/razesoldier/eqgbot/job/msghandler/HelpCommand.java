/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.job.msghandler;

import net.mamoe.mirai.event.events.GroupMessageEvent;

class HelpCommand implements MessageHandler {
    private final GroupMessageEvent event;

    public HelpCommand(GroupMessageEvent event) {
        this.event = event;
    }

    @Override
    public void handle() throws Exception {
        event.getGroup().sendMessage("""
                .status -- 显示游戏服务器状态
                .me -- 显示我的角色名
                .help -- 本帮助
                .gittime ddhhmm -- 计算增强时间""");
    }
}
