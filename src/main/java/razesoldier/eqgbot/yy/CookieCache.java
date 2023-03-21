/*
 * Copyright 2019-2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.yy;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CookieCache {
    private static final String FILENAME = "cookies.txt";
    private static CookieCache instance;

    private CookieCache() {
    }

    public static CookieCache getInstance() {
        if (instance == null) {
            instance = new CookieCache();
        }
        return instance;
    }

    public boolean exists() {
        return new File(FILENAME).exists();
    }

    public String getContent() throws IOException {
        return Files.readString(Path.of(FILENAME));
    }

    public void save(@NotNull String content) throws IOException {
        Files.writeString(Path.of(FILENAME), content);
    }
}
