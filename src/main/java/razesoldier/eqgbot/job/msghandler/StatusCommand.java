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
import net.troja.eve.esi.model.StatusResponse;
import razesoldier.eqgbot.esi.EsiException;
import razesoldier.eqgbot.esi.EsiFacade;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

class StatusCommand implements MessageHandler {
    private final GroupMessageEvent event;

    public StatusCommand(GroupMessageEvent event) {
        this.event = event;
    }

    @Override
    public void handle() {
        StatusResponse resp;
        try {
            resp = getServerStatus();
        } catch (EsiException e) {
            event.getGroup().sendMessage("无法连接ESI服务，可能是因为服务器正在维护");
            return;
        }
        String startTime = resp.getStartTime().atZoneSameInstant(ZoneId.of("+8")).format(DateTimeFormatter.ISO_LOCAL_TIME);
        String builder = "宁静服务器状态\n" + "在线玩家：" + resp.getPlayers() + "\n" +
                "服务器启动时间：" + startTime;
        event.getGroup().sendMessage(builder);
    }

    private StatusResponse getServerStatus() throws EsiException {
        return EsiFacade.getServerStatus();
    }
}
