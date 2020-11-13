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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeatureRegister {
    private static final Map<String, Class<? extends Feature>> featureMap = Map.of(
            "listenJoinGroupRequest", ListenJoinGroupRequest.class,
            "listenGroupMessage", ListenGroupMessage.class,
            "cronKickInvalidMember", CronKickInvalidMember.class,
            "listenSrpRightRequest", ListenSrpRightRequest.class,
            "sovAlert", SovAlert.class
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
        final var canonicalName = featureClass.getCanonicalName();
        if (canonicalName.equals(ListenJoinGroupRequest.class.getCanonicalName())) {
            var obj = new ListenJoinGroupRequest(bot, groupMap, logger);
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (canonicalName.equals(ListenGroupMessage.class.getCanonicalName())) {
            var obj = new ListenGroupMessage(bot);
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (canonicalName.equals(CronKickInvalidMember.class.getCanonicalName())) {
            var obj = new CronKickInvalidMember(bot, logger);
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (canonicalName.equals(ListenSrpRightRequest.class.getCanonicalName())) {
            var obj = new ListenSrpRightRequest(bot, logger);
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (canonicalName.equals(SovAlert.class.getCanonicalName())) {
            var obj = new SovAlert(bot, logger, config.getSovAlertGroup());
            obj.setEnabled(true);
            featureQueue.add(obj);
        }
    }
}
