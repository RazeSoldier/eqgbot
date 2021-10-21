/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot;

import com.alibaba.fastjson.JSON;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.dba.DatabaseAccessHolding;
import razesoldier.eqgbot.feature.FeatureRegister;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * QQ机器人的主类。
 * 此类由{@link Entry}入口类引导并调用。
 */
class Bot {
    private final Config config;
    private final String deviceInfoPath;

    Bot(@NotNull String configPath, @NotNull String deviceInfoPath, @NotNull String whitelistPath) throws Exception {
        config = JSON.parseObject(Files.readString(Path.of(configPath)), Config.class);
        this.deviceInfoPath = deviceInfoPath;
        QQWhiteList.init(new File(whitelistPath));
        DatabaseAccessHolding.initService(config.getOfDatabaseConfig(), config.getGfDatabaseConfig());
    }

    public void run() throws IOException {
        final var account = config.getAccount();
        net.mamoe.mirai.Bot bot = BotFactory.INSTANCE.newBot(account.getId(), account.getPassword(), getBotConfig());

        MiraiLogger logger = new Logger(new File(System.getProperty("user.dir") + "/eqgbot.log"));
        GroupMap groupMap = initGroupMap();

        FeatureRegister featureRegister = new FeatureRegister(bot, groupMap, logger, config);
        config.getFeatures().forEach(featureRegister::enable);

        bot.login();
        featureRegister.register();
        bot.join();
    }

    @NotNull
    private BotConfiguration getBotConfig() {
        var botConfig = new BotConfiguration();
        botConfig.fileBasedDeviceInfo(deviceInfoPath);
        botConfig.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
        return botConfig;
    }

    @NotNull
    private GroupMap initGroupMap() {
        var groupMap = new GroupMap();
        var pingGroupConfig = config.getPingGroups();
        var ofGroupNumber = pingGroupConfig.get("of");
        var gfGroupNumber = pingGroupConfig.get("gf");
        if (ofGroupNumber != null) {
            groupMap.put(new Group(ofGroupNumber, GameServer.OF));
        }
        if (gfGroupNumber != null) {
            groupMap.put(new Group(gfGroupNumber, GameServer.GF));
        }
        return groupMap;
    }
}
