/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.job;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

public class CheckCharacterNotificationTest {
    @Test
    void testGetSystemName() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var method = CheckCharacterNotification.class.getDeclaredMethod("getSystemName", Integer.class);
        method.setAccessible(true);
        assertEquals("ION-FG", method.invoke(null, 30001991));
    }
}
