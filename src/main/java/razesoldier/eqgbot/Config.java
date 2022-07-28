/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Config {
    private List<String> features = new ArrayList<>();
    private DatabaseConfig gfDatabaseConfig = new DatabaseConfig();
    private QQAccount account = new QQAccount();
    private int sovAlertGroup;
    private String sovAlertQueueKey;
    private Map<String, Integer> pingGroups;
    private String redisPassword;
    private List<Long> vettedGroupList;
    private List<Long> pingGroupList;
    private String structureAlertQueueKey;
    private List<Long> structureAlertGroups;
    private List<DMRelayMap> dmRelayList;
    private Long titanGroup;

    @Data
    public static class DatabaseConfig {
        private String serverName;
        private String databaseName;
        private String user;
        private String password;
    }

    @Data
    public static class QQAccount {
        private long id;
        private String password;
    }

    @Data
    public static class DMRelayMap {
        private long src;
        private long dest;
    }
}
