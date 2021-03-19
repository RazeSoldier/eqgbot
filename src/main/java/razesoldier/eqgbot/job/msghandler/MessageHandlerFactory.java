/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.job.msghandler;

import net.mamoe.mirai.message.GroupMessageEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class MessageHandlerFactory {
    private final GroupMessageEvent event;
    private static final Map<String, Class<? extends MessageHandler>> commandMap = Map.of(
            ".me", MeCommand.class,
            ".pap", PapCommand.class,
            ".status", StatusCommand.class,
            ".help", HelpCommand.class
    );//无参数

    private static final Map<String, Class<? extends MessageHandler>> commandMap_p = Map.of(
            ".gittime", GitTimeCommand.class
    );//有参数

    private MessageHandlerFactory(GroupMessageEvent event) {
        this.event = event;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static MessageHandlerFactory newInstance(@NotNull GroupMessageEvent event) {
        return new MessageHandlerFactory(event);
    }

    @NotNull
    public MessageHandler make(@NotNull String command) throws UnknownCommandException {
        Class<? extends MessageHandler> className = null;
        //有参数
        if(command.indexOf(" ") == 1) {
            String[] commandArray = command.split(" ");
            className = commandMap_p.get(commandArray[0]);
            if (className == null) {
                throw new UnknownCommandException(commandArray[0]);
            }
        }
        //无参数
        if(command.indexOf(" ") == -1) {
            className = commandMap.get(command);
            if (className == null) {
                throw new UnknownCommandException(command);
            }
        }

        try {
            return className.getConstructor(event.getClass()).newInstance(event);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
