/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 机器人的入口点。应用程序应该从这里启动。
 */
public class Entry {
    public static void main(String[] argv) throws Exception {
        var options = new CommandLineOptions();
        JCommander.newBuilder().addObject(options).build().parse(argv);
        // 当命令行存在参数时代表执行一次性命令后退出
        // 第一个参数为命令名
        new Bot(options.configPath, options.deviceInfoPath, options.parameters).run();
    }

    static class CommandLineOptions {
        @Parameter
        List<String> parameters = new ArrayList<>();

        @Parameter(names = "-d", description = "Path to the device info file", required = true,
                validateWith = ValidateFile.class)
        String deviceInfoPath;

        @Parameter(names = "-c", description = "Path to the config file", required = true,
            validateWith = ValidateFile.class)
        String configPath;
    }

    public static class ValidateFile implements IParameterValidator {
        @Override
        public void validate(String name, String value) throws ParameterException {
            if (!new File(value).exists()) {
                throw new ParameterException(value + " is not a file");
            }
        }
    }
}
