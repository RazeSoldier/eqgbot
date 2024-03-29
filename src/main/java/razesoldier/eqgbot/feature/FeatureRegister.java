/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
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
import razesoldier.eqgbot.queue.MessageQueueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeatureRegister {
    private static final Map<String, Class<? extends Feature>> featureMap = Map.of(
            "listenJoinGroupRequest", ListenJoinGroupRequest.class,
            "sovAlert", SovAlert.class,
            "searchEsiUser", SearchEsiUser.class,
            "exportInvalidGroupMembers", ExportInvalidPingGroupMembers.class,
            "structureAlert", StructureAlert.class,
            "relayDM2Group", RelayDM2Group.class,
            "listenJoinTitanGroupRequest", ListenJoinTitanGroupRequest.class,
            "messageBroadcast", MessageBroadcast.class,
            "manageYYAdmin", ManageYYAdmin.class
    );
    private static final List<Feature> featureQueue = new ArrayList<>();
    private final Bot bot;
    private final MiraiLogger logger;
    private final Config config;

    public FeatureRegister(Bot bot, MiraiLogger logger, Config config) {
        this.bot = bot;
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
            var obj = new ListenJoinGroupRequest(bot, config.getVettedGroupList(), config.getPingGroupList(), logger);
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (classEquals(featureClass, SovAlert.class)) {
            var obj = new SovAlert(bot, config.getSovAlertGroup(), MessageQueueFactory.newSovAlertQueue(config));
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (classEquals(featureClass, SearchEsiUser.class)) {
            var obj = new SearchEsiUser(bot);
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (classEquals(featureClass, ExportInvalidPingGroupMembers.class)) {
            var obj = new ExportInvalidPingGroupMembers(bot, config.getPingGroups().get("gf"));
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (classEquals(featureClass, StructureAlert.class)) {
            var obj = new StructureAlert(bot, config.getStructureAlertGroups(), MessageQueueFactory.newStructureAlertQueue(config));
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (classEquals(featureClass, RelayDM2Group.class)) {
            var obj = new RelayDM2Group(bot, config.getDmRelayList());
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (classEquals(featureClass, ListenJoinTitanGroupRequest.class)) {
            var obj = new ListenJoinTitanGroupRequest(bot, config.getTitanGroup(), logger);
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (classEquals(featureClass, MessageBroadcast.class)) {
            var obj = new MessageBroadcast(bot, config.getMessageBroadcast());
            obj.setEnabled(true);
            featureQueue.add(obj);
            return;
        }
        if (classEquals(featureClass, ManageYYAdmin.class)) {
            if (config.getYyLoginCredential() == null || config.getYyManagedGroup() == null) {
                return;
            }
            var obj = new ManageYYAdmin(bot, config.getYyManagedGroup(), config.getYyLoginCredential());
            obj.setEnabled(true);
            featureQueue.add(obj);
        }
    }

    private boolean classEquals(@NotNull Class<? extends Feature> a, @NotNull Class<? extends Feature> b) {
        return a.getCanonicalName().equals(b.getCanonicalName());
    }
}
