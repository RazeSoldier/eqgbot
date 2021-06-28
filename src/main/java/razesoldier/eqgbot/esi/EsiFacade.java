/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.esi;

import net.troja.eve.esi.model.StatusResponse;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class EsiFacade {
    public static StatusResponse getServerStatus() throws EsiException {
        return getClient().getServerStatus();
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    private static EsiClient getClient() {
        return new EsiClientImp();
    }
}
