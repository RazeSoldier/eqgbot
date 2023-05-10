/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot;

import com.alibaba.fastjson2.JSON;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.dba.DatabaseAccessHolding;
import razesoldier.eqgbot.feature.FeatureRegister;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * QQ机器人的主类。
 * 此类由{@link Entry}入口类引导并调用。
 */
class Bot {
    private final Config config;
    private final String deviceInfoPath;
    private final List<String> cliParameters;

    Bot(@NotNull String configPath, @NotNull String deviceInfoPath, List<String> cliParameters)
            throws Exception {
        config = JSON.parseObject(Files.readString(Path.of(configPath)), Config.class);
        this.cliParameters = cliParameters;
        this.deviceInfoPath = deviceInfoPath;
        DatabaseAccessHolding.initService(config.getGfDatabaseConfig());
    }

    public void run() throws IOException {
        net.mamoe.mirai.Bot bot = new MiraiBotBuilder(config, deviceInfoPath).build();

        MiraiLogger logger = new Logger(new File(System.getProperty("user.dir") + "/eqgbot.log"));
        FeatureRegister featureRegister = new FeatureRegister(bot, logger, config);

        if (isDaemon()) {
            config.getFeatures().forEach(featureRegister::enable);
            bot.login();
            featureRegister.register();
            bot.join();
        } else {
            featureRegister.enable(cliParameters.get(0));
            bot.login();
            featureRegister.register();
            bot.close();
        }
    }

    /**
     * 如果没有命令行参数，则说明以守护进程运行
     */
    private boolean isDaemon() {
        return this.cliParameters.isEmpty();
    }

    private static class MiraiBotBuilder {
        private final Config config;
        private final String deviceInfoPath;

        MiraiBotBuilder(Config config, String deviceInfoPath) {
            this.config = config;
            this.deviceInfoPath = deviceInfoPath;
        }

        @NotNull
        net.mamoe.mirai.Bot build() {
            return BotFactory.INSTANCE.newBot(config.getAccount().getId(), buildBotAuth(), buildBotConfig());
        }

        @NotNull
        private BotAuthorization buildBotAuth() {
            BotAuthorization botAuthorization;
            String loginMethod = config.getLoginMethod();
            // 默认使用密码登录
            if (loginMethod != null && loginMethod.equals("qrcode")) {
                botAuthorization = BotAuthorization.byQRCode();
            } else {
                botAuthorization = BotAuthorization.byPassword(config.getAccount().getPassword());
            }
            return botAuthorization;
        }

        @NotNull
        private BotConfiguration buildBotConfig() {
            var botConfig = new BotConfiguration();
            botConfig.fileBasedDeviceInfo(deviceInfoPath);
            // 默认使用手表协议
            BotConfiguration.MiraiProtocol protocol = switch (config.getLoginProtocol()) {
                case "pad" -> BotConfiguration.MiraiProtocol.ANDROID_PAD;
                case "phone" -> BotConfiguration.MiraiProtocol.ANDROID_PHONE;
                case "ipad" -> BotConfiguration.MiraiProtocol.IPAD;
                case "macos" -> BotConfiguration.MiraiProtocol.MACOS;
                default -> BotConfiguration.MiraiProtocol.ANDROID_WATCH;
            };
            botConfig.setProtocol(protocol);
            return botConfig;
        }
    }
}
