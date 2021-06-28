/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * QQ号白名单。在此白名单的QQ号将会在{@link razesoldier.eqgbot.job.CheckUser}的检查中被忽略.
 */
public class QQWhiteList {
    private static QQWhiteList instance;
    private final List<Long> list = new ArrayList<>();

    public static void init(File file) throws IOException {
        instance = new QQWhiteList(file);
    }

    @NotNull
    public static QQWhiteList getInstance() {
        return instance;
    }

    private QQWhiteList(File file) throws IOException {
        String text = Files.readString(file.toPath());
        Arrays.stream(StringUtils.split(text, "\n")).forEach(s -> {
            list.add(Long.valueOf(s));
        });
    }

    public boolean has(long id) {
        return list.contains(id);
    }
}
