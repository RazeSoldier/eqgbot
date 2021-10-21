/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {
    private List<String> features = new ArrayList<>();
    private DatabaseConfig gfDatabaseConfig = new DatabaseConfig();
    private DatabaseConfig ofDatabaseConfig = new DatabaseConfig();
    private QQAccount account = new QQAccount();
    private int sovAlertGroup;
    private String sovAlertQueueKey;
    private int badCharacterNoticeGroup;
    private Map<String, Integer> pingGroups;

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public DatabaseConfig getGfDatabaseConfig() {
        return gfDatabaseConfig;
    }

    public void setGfDatabaseConfig(DatabaseConfig gfDatabaseConfig) {
        this.gfDatabaseConfig = gfDatabaseConfig;
    }

    public DatabaseConfig getOfDatabaseConfig() {
        return ofDatabaseConfig;
    }

    public void setOfDatabaseConfig(DatabaseConfig ofDatabaseConfig) {
        this.ofDatabaseConfig = ofDatabaseConfig;
    }

    public QQAccount getAccount() {
        return account;
    }

    public void setAccount(QQAccount account) {
        this.account = account;
    }

    public void setSovAlertGroup(int sovAlertGroup) {
        this.sovAlertGroup = sovAlertGroup;
    }

    public int getSovAlertGroup() {
        return sovAlertGroup;
    }

    public int getBadCharacterNoticeGroup() {
        return badCharacterNoticeGroup;
    }

    public void setBadCharacterNoticeGroup(int badCharacterNoticeGroup) {
        this.badCharacterNoticeGroup = badCharacterNoticeGroup;
    }

    public String getSovAlertQueueKey() {
        return sovAlertQueueKey;
    }

    public void setSovAlertQueueKey(String sovAlertQueueKey) {
        this.sovAlertQueueKey = sovAlertQueueKey;
    }

    public Map<String, Integer> getPingGroups() {
        return pingGroups;
    }

    public void setPingGroups(Map<String, Integer> pingGroups) {
        this.pingGroups = pingGroups;
    }

    public static class DatabaseConfig {
        private String serverName;
        private String databaseName;
        private String user;
        private String password;

        public String getServerName() {
            return serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        public void setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class QQAccount {
        private long id;
        private String password;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
