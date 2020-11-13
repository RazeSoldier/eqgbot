/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.job;

import com.alibaba.fastjson.JSON;
import io.timeandspace.cronscheduler.CronTask;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.MiraiLogger;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import razesoldier.eqgbot.GameServer;
import razesoldier.eqgbot.dba.DatabaseAccessHolding;
import razesoldier.eqgbot.esi.CharacterAuthentication;
import razesoldier.eqgbot.esi.EsiException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检查角色的通知列表来实现主权预警。
 */
public class CheckCharacterNotification implements Job, CronTask {
    private static Instant lastRunInstant = Instant.now();
    private final static HttpClient httpClient = HttpClient.newHttpClient();
    private final Bot bot;
    private final MiraiLogger logger;
    private final int groupId;

    public CheckCharacterNotification(Bot bot, MiraiLogger logger, int groupId) {
        this.bot = bot;
        this.logger = logger;
        this.groupId = groupId;
    }

    @Override
    public void run(long scheduledRunTimeMillis) throws Exception {
        var cAuthList = getCharacterAuthenticationFromDB();
        Set<Notification> notificationSet = new HashSet<>();

        cAuthList.forEach(a -> {
            try {
                var list = getCharacterNotification(a);
                if (list == null) {
                    return;
                }
                list.stream()
                        .filter(notification -> notification.type.equals("EntosisCaptureStarted"))
                        .filter(notification -> ZonedDateTime.parse(notification.timestamp).toInstant().isAfter(lastRunInstant))
                        .forEach(notificationSet::add);
            } catch (EsiException e) {
                logger.warning(e);
            }
        });
        lastRunInstant = Instant.now();

        notificationSet.forEach(notification -> {
            var text = notification.text;
            // 匹配到星系ID @{
            Pattern r = Pattern.compile("solarSystemID: ([0-9]*)");
            Matcher m = r.matcher(text);
            if (!m.find()) {
                return;
            }
            String systemName;
            try {
                systemName = getSystemName(Integer.valueOf(m.group(1)));
            } catch (EsiException e) {
                logger.warning(e);
                return;
            }
            // @}
            // 匹配主权设施的类型 @{
            r = Pattern.compile("structureTypeID: ([0-9]*)");
            m = r.matcher(text);
            if (!m.find()) {
                return;
            }
            String type = switch (m.group(1)) {
                case "32458" -> "i-hub";
                case "32226" -> "TCU";
                default -> throw new RuntimeException("Unsupported structureTypeID: " + m.group(1));
            };
            // @}
            var msg = String.format("[主权预警] %s的%s正在被入侵", systemName, type);
            bot.getGroup(groupId).sendMessage(msg);
        });
    }

    @NotNull
    private List<CharacterAuthentication> getCharacterAuthenticationFromDB() throws SQLException {
        List<CharacterAuthentication> authenticationList = new ArrayList<>();

        var conn = DatabaseAccessHolding.getInstance().getConnection(GameServer.GF);
        var set = DatabaseAccessHolding.executeQuery(
                conn,
                "select tokens.character_id,tokens.value from tokens,characters " +
                        "where tokens.scopes like '%esi-characters.read_notifications.v1%' and tokens.valid=1 and tokens.character_id=characters.id"
        );
        while (set.next()) {
            authenticationList.add(CharacterAuthentication.of(
                    set.getInt("tokens.character_id"),
                    List.of("esi-characters.read_notifications.v1"),
                    set.getString("tokens.value")
            ));
        }

        return authenticationList;
    }

    @Nullable
    private List<Notification> getCharacterNotification(CharacterAuthentication authentication) throws EsiException {
        var accessToken = getAccessToken(authentication).access_token;
        if (accessToken == null) {
            return null;
        }
        var url = String.format("https://esi.evepc.163.com/v6/characters/%d/notifications/?datasource=serenity", authentication.getCharacterId());
        var request = HttpRequest.newBuilder().uri(URI.create(url)).header("Authorization", "Bearer " + accessToken).build();
        try {
            var json = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return JSON.parseArray(json, Notification.class);
        } catch (IOException | InterruptedException e) {
            throw new EsiException(e);
        }
    }

    private TokenModel getAccessToken(@NotNull CharacterAuthentication authentication) throws EsiException {
        String paramBuilder = "grant_type=refresh_token" + "&refresh_token=" + authentication.getToken() + "&client_id=bc90aa496a404724a93f41b4f4e97761" +
                "&scope=" + StringUtils.join(authentication.getScopes(), ",");
        var request = HttpRequest.newBuilder().uri(URI.create("https://login.evepc.163.com/v2/oauth/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(paramBuilder))
                .build();
        HttpResponse<String> resp;
        try {
            resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new EsiException(e);
        }
        return JSON.parseObject(resp.body(), TokenModel.class);
    }

    private static String getSystemName(Integer systemId) throws EsiException {
        var url = String.format("https://esi.evepc.163.com/v4/universe/systems/%d/?datasource=serenity", systemId);
        var request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        try {
            var json = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return JSON.parseObject(json, SolarSystem.class).name;
        } catch (IOException | InterruptedException e) {
            throw new EsiException(e);
        }
    }

    public static class TokenModel {
        public String access_token;
        public Integer expires_in;
        public String token_type;
        public String refresh_token;
    }

    public static class Notification {
        public Integer notification_id;
        public Integer sender_id;
        public String sender_type;
        public String text;
        public String timestamp;
        public String type;

        @Override
        public String toString() {
            return "Notification{" +
                    "notification_id=" + notification_id +
                    ", sender_id=" + sender_id +
                    ", sender_type='" + sender_type + '\'' +
                    ", text='" + text + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    private static class SolarSystem {
        public String name;
    }
}
