/*
 * Copyright 2019-2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.yy;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class YYOperationResult {
    private final boolean success;
    private final YYAccount yyAccount;
    private final String message;

    private YYOperationResult(boolean success, YYAccount yyAccount, String message) {
        this.success = success;
        this.yyAccount = yyAccount;
        this.message = message;
    }

    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    public static YYOperationResult newSuccess(YYAccount yyAccount, String message) {
        return new YYOperationResult(true, yyAccount, message);
    }

    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    public static YYOperationResult newFail(YYAccount yyAccount, String message) {
        return new YYOperationResult(false, yyAccount, message);
    }

    @Override
    public String toString() {
        return message;
    }
}
