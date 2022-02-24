/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot;

import net.mamoe.mirai.utils.PlatformLogger;
import net.mamoe.mirai.utils.SimpleLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class Logger extends PlatformLogger {
    private final File file;

    Logger(@NotNull File file) throws IOException {
        super("App");
        if (!file.exists()) {
            Files.createFile(file.toPath());
        }
        this.file = file;
    }

    @Override
    protected void printLog(@Nullable String message, @NotNull SimpleLogger.LogPriority priority) {
        if (message == null) {
            return;
        }

        try {
            String s = LocalDateTime.now().format(getFormatter()) + " " + priority.getSimpleName() + "/" +
                    getIdentity() + ": " + message + "\n";
            Files.writeString(file.toPath(), s, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
