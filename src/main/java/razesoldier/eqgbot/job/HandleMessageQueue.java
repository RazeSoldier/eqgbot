/*
 * Copyright 2019-2022 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.job;

import io.timeandspace.cronscheduler.CronTask;
import net.mamoe.mirai.contact.Group;
import razesoldier.eqgbot.queue.MessageQueue;

import java.util.List;

public class HandleMessageQueue implements CronTask {
    private final MessageQueue messageQueue;
    private final List<Group> contacts;

    public HandleMessageQueue(MessageQueue messageQueue, List<Group> contacts) {
        this.messageQueue = messageQueue;
        this.contacts = contacts;
    }

    @Override
    public void run(long scheduledRunTimeMillis) {
        messageQueue.getMessage().ifPresent(message -> contacts.forEach(contact -> contact.sendMessage(message)));
    }
}
