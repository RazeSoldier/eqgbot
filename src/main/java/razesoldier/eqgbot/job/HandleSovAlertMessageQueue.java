/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.job;

import io.timeandspace.cronscheduler.CronTask;
import net.mamoe.mirai.contact.Contact;
import razesoldier.eqgbot.queue.MessageQueue;

/**
 * 处理主权警告的消息队列。把队列里的消息拿出来，然后发送到对应的联系人。
 */
public class HandleSovAlertMessageQueue implements CronTask {
    private final MessageQueue messageQueue;
    private final Contact contact;

    public HandleSovAlertMessageQueue(MessageQueue messageQueue, Contact contact) {
        this.messageQueue = messageQueue;
        this.contact = contact;
    }

    @Override
    public void run(long scheduledRunTimeMillis) {
        messageQueue.getMessage().ifPresent(contact::sendMessage);
    }
}
