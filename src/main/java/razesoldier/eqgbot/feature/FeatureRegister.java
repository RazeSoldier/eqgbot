/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.feature;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.Config;
import razesoldier.eqgbot.GroupMap;
import razesoldier.eqgbot.queue.MessageQueueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeatureRegister {
    private static final Map<String, Class<? extends Feature>> featureMap = Map.of(
            "listenJoinGroupRequest", ListenJoinGroupRequest.class,
            "listenGroupMessage", ListenGroupMessage.class,
            "cronKickInvalidMember", CronKickInvalidMember.class,
            "sovAlert", SovAlert.class,
            "-10tracking", BadCharacterTracking.class,
            "searchEsiUser", SearchEsiUser.class
    );
    private static final List<Feature> featureQueue = new ArrayList<>();
    private final Bot bot;
    private final GroupMap groupMap;
    private final MiraiLogger logger;
    private final Config config;

    public FeatureRegister(Bot bot, GroupMap groupMap, MiraiLogger logger, Config config) {
        this.bot = bot;
        this.groupMap = groupMap;
        this.logger = logger;
        this.config = config;
    }

    public void register() {
        featureQueue.forEach(Feature::call);
    }

    public void enable(String feature) {
        if (!featureMap.containsKey(feature)) {
            throw new RuntimeException("Feature " + feature + " is undefined");
        }
        initFeature(featureMap.get(feature));
    }

    private void initFeature(@NotNull Class<? extends Feature> featureClass) {
        if (classEquals(featureClass, ListenJoinGroupRequest.class)) {
            var obj = new ListenJoinGroupRequest(bot, groupMap, logger);
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (classEquals(featureClass, ListenGroupMessage.class)) {
            var obj = new ListenGroupMessage(bot);
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (classEquals(featureClass, CronKickInvalidMember.class)) {
            var obj = new CronKickInvalidMember(bot, logger);
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (classEquals(featureClass, SovAlert.class)) {
            var obj = new SovAlert(bot, config.getSovAlertGroup(), new MessageQueueFactory().newInstance(config));
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (classEquals(featureClass, BadCharacterTracking.class)) {
            var obj = new BadCharacterTracking(bot, logger, config.getBadCharacterNoticeGroup());
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (classEquals(featureClass, SearchEsiUser.class)) {
            var obj = new SearchEsiUser(bot);
            obj.setEnabled(true);
            featureQueue.add(obj);
        }
    }

    private boolean classEquals(@NotNull Class<? extends Feature> a, @NotNull Class<? extends Feature> b) {
        return a.getCanonicalName().equals(b.getCanonicalName());
    }
}
