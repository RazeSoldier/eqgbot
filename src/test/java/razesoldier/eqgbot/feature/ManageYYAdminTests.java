/*
 * Copyright 2019-2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.feature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

class ManageYYAdminTests {
    @SuppressWarnings("unchecked")
    @Test
    void testMatchAssignRole() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var method = ManageYYAdmin.class.getDeclaredMethod("matchAssignRole", String.class);
        method.setAccessible(true);
        Optional<Long> res = (Optional<Long>) method.invoke(new ManageYYAdmin(null, null, null), "发黄马 123456");
        Assertions.assertTrue(res.isPresent());
        Assertions.assertEquals(123456, res.get());

        res = (Optional<Long>) method.invoke(new ManageYYAdmin(null, null, null), "发黄马 123456.");
        Assertions.assertTrue(res.isEmpty());
    }
}
