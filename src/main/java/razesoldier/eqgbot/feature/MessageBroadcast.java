/*
 * Copyright 2019-2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.feature;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.Config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageBroadcast extends FeatureBase {
    private final Bot bot;
    private final Config.MessageBroadcast messageBroadcastConfig;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public MessageBroadcast(Bot bot, Config.MessageBroadcast messageBroadcastConfig) {
        this.bot = bot;
        this.messageBroadcastConfig = messageBroadcastConfig;
    }

    @Override
    void handle() {
        bot.getEventChannel()
                .filterIsInstance(GroupMessageEvent.class)
                .filter(group -> messageBroadcastConfig.getUpstream() == group.getGroup().getId())
                .subscribeAlways(GroupMessageEvent.class, groupMessageEvent -> {
                    if (groupMessageEvent.getMessage().contentEquals("怎么用", true)) {
                        printHelpMessage(groupMessageEvent.getGroup());
                        return;
                    }
                    dispatchAction(groupMessageEvent);
                });
    }

    private void dispatchAction(@NotNull GroupMessageEvent groupMessageEvent) {
        var message = groupMessageEvent.getMessage().contentToString();

        if (message.startsWith("通知_")) {
            var messageChainBuilder = new MessageChainBuilder().append("※※※  新通知  ※※※\n")
                    .append(LocalDateTime.now().format(DATE_FORMAT))
                    .append("\n发送者：")
                    .append(groupMessageEvent.getSender().getNameCard())
                    .append("\n\n");
            // 用来替换第一行的命令——“通知_”
            AtomicBoolean isFirstLine = new AtomicBoolean(true);
            groupMessageEvent.getMessage()
                    .forEach(singleMessage -> {
                        if (singleMessage instanceof PlainText text) {
                            var textContent = text.getContent();
                            if (isFirstLine.get()) {
                                textContent = textContent.replaceFirst("通知_", "");
                                isFirstLine.set(false); // 只替换一次
                            }
                            messageChainBuilder.add(textContent);
                        } else if (singleMessage instanceof Image image) {
                            messageChainBuilder.add(image);
                        }
                    });
            sendMessageToDownstream(messageChainBuilder.build());
        }
    }

    private void printHelpMessage(@NotNull Group upstream) {
        upstream.sendMessage("通知_通知内容");
    }

    private void sendMessageToDownstream(MessageChain messageChain) {
        this.messageBroadcastConfig
                .getDownstream()
                .stream()
                .map(bot::getGroup)
                .filter(Objects::nonNull)
                .forEach(group -> group.sendMessage(messageChain));
    }
}
