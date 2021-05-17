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
import net.mamoe.mirai.event.events.TempMessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.EVEUser;
import razesoldier.eqgbot.GameServer;
import razesoldier.eqgbot.dba.DatabaseAccessHolding;
import razesoldier.eqgbot.dba.SQLExecuteException;

import java.sql.Connection;
import java.sql.SQLException;

public class HandleSrpRightRequest extends SimpleListenerHost implements Job {
    private final MiraiLogger logger;

    public HandleSrpRightRequest(MiraiLogger logger) {
        this.logger = logger;
    }

    @EventHandler
    public void onTempMessage(TempMessageEvent event) {
        final var msg = event.getMessage().contentToString();
        if (!msg.equals(".srp")) {
            return;
        }

        EVEUser user;
        try {
            user = EVEUser.newInstanceFromOF(event.getSender().getId());
        } catch (SQLExecuteException e) {
            logger.error(e);
            event.getSender().sendMessage("机器人出错，请报告给“勒维-星耀晨曦”");
            return;
        }
        if (user == null) {
            event.getSender().sendMessage("未注册SEAT或者尚未绑定QQ");
            return;
        }
        final var allianceName = user.getAllianceName();
        if (!allianceName.equals("VENI VIDI VICI.") && !allianceName.equals("The Stars of northern moon")) {
            event.getSender().sendMessage("非法用户");
            return;
        }

        try (Connection conn = DatabaseAccessHolding.getInstance().getConnection(GameServer.OF)) {
            int groupId;
            {
                var set = DatabaseAccessHolding.executeQuery(
                        conn, "select group_id from users where id=" + user.getId()
                );
                if (!set.next()) {
                    event.getSender().sendMessage("找不到Group，请报告给“勒维-星耀晨曦”");
                    return;
                }
                groupId = set.getInt("group_id");
            }
            var set = DatabaseAccessHolding.executeQuery(conn, "select group_role.role_id from group_role,users\n" +
                    "where users.group_id=group_role.group_id and group_role.role_id=27 and users.id=" + user.getId());
            if (set.next()) {
                event.getSender().sendMessage("已经拥有权限");
                return;
            }
            var raw = DatabaseAccessHolding.executeUpdate(
                    conn, "insert into group_role (role_id, group_id) values (27, " + groupId + ")"
            );
            if (raw == 1) {
                event.getSender().sendMessage("成功");
            } else {
                event.getSender().sendMessage("失败");
            }
        } catch (SQLException throwables) {
            logger.error(throwables);
            event.getSender().sendMessage("机器人出错，请报告给“勒维-星耀晨曦”");
        }
    }

    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }
}
