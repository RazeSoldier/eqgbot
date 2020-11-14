/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.job;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.EVEUser;
import razesoldier.eqgbot.GameServer;
import razesoldier.eqgbot.Group;
import razesoldier.eqgbot.GroupMap;

/**
 * 处理进群请求。
 */
public class HandleJoinGroupEvent extends SimpleListenerHost implements Job {
    private final GroupMap groupMap;
    private final MiraiLogger logger;

    public HandleJoinGroupEvent(GroupMap groupMap, MiraiLogger logger) {
        this.groupMap = groupMap;
        this.logger = logger;
    }

    @EventHandler
    public void onRequestJoinGroup(MemberJoinRequestEvent event) throws Exception {
        try {
            // 判断接受到的事件是要监听的群
            final var groupId = event.getGroupId();
            if (!groupMap.hasGroup(groupId)) {
                return;
            }

            final Group group = groupMap.get(groupId);
            final var fromId = event.getFromId();
            logger.info(groupId + ": 接受到" + fromId + "的入群请求");
            EVEUser user;
            if (group.getServer() == GameServer.GF) {
                user = EVEUser.newInstanceFromGF(fromId, 562593865);
            } else {
                user = EVEUser.newInstanceFromOF(fromId);
            }

            if (user != null) {
                if (group.getServer() == GameServer.GF && !user.getAllianceName().equals("VENI VIDI VICI")) {
                    logger.info(groupId + ": 拒绝" + fromId + "的入群请求,原因:角色不在主联盟");
                    event.reject(false, "查询不到QQ绑定记录");
                    return;
                }
                event.accept(); // 接受请求
                logger.info(groupId + ": 接受" + fromId + "的入群请求");
                // 并发送“军团-角色名”到群聊
                if (group.getServer() == GameServer.OF) {
                    event.getGroup().sendMessage(user.getCorpName() + '-' + user.getName() + "，进群后请屏蔽本机器人");
                } else {
                    event.getGroup().sendMessage("欢迎加入VVV国服集结群，" +
                            user.getCorpName() + '-' + user.getName() + "，进群后请屏蔽本机器人");
                }
            } else {
                logger.info(groupId + ": 拒绝" + fromId + "的入群请求,原因:查询不到QQ绑定记录");
                event.reject(false, "查询不到QQ绑定记录");
            }
        } catch (Exception e) {
            event.reject(false, "机器人出错啦，请等待开发维修完毕~");
            throw new Exception(e);
        }
    }

    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }
}
