/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
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

/**
 * 机器人的入口点。应用程序应该从这里启动。
 */
public class Entry {
    public static void main(String[] argv) throws Exception {
        var options = new CommandLineOptions();
        JCommander.newBuilder().addObject(options).build().parse(argv);
        new Bot(options.configPath, options.deviceInfoPath, options.whitelistPath).run();
    }

    static class CommandLineOptions {
        @Parameter(names = "-d", description = "Path to the device info file", required = true,
                validateWith = ValidateFile.class)
        String deviceInfoPath;

        @Parameter(names = "-c", description = "Path to the config file", required = true,
            validateWith = ValidateFile.class)
        String configPath;

        @Parameter(names = "-w", description = "Path to the whitelist file", required = true,
            validateWith = ValidateFile.class)
        String whitelistPath;
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
