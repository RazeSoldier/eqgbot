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
import net.mamoe.mirai.message.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.job.msghandler.MessageHandlerFactory;
import razesoldier.eqgbot.job.msghandler.UnknownCommandException;

public class HandleGroupMessageEvent extends SimpleListenerHost implements Job {
    private final long groupId;

    /**
     * @param groupId 要监听的Q群号
     */
    public HandleGroupMessageEvent(long groupId) {
        this.groupId = groupId;
    }

    @EventHandler
    public void handleGroupMessage(GroupMessageEvent event) {
        if (event.getGroup().getId() != groupId) {
            return;
        }
        var msg = event.getMessage().contentToString();
        if (msg.charAt(0) != '.') {
            return;
        }
        var msgFactory = MessageHandlerFactory.newInstance(event);
        try {
            msgFactory.make(msg).handle();
        } catch (UnknownCommandException e) {
            event.getBot().getLogger().info(e);
        } catch (Exception e) {
            event.getBot().getLogger().error(e);
            event.getGroup().sendMessage(e.getMessage());
        }
    }

    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }
}
