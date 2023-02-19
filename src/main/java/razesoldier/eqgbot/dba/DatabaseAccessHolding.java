/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.dba;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.jdbc.MysqlDataSourceFactory;
import org.apache.commons.lang3.time.TimeZones;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.Config;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 此类用来管理应用程序的数据库连接。
 * 应该用此类来访问数据库。
 */
public class DatabaseAccessHolding {
    private static DatabaseAccessHolding instance;
    private final DataSource dataSources;

    private DatabaseAccessHolding(@NotNull Config.DatabaseConfig databaseConfig) throws Exception {
        final var className = MysqlDataSource.class.getName();

        var configRef = new Reference(className);
        configRef.add(new StringRefAddr("serverName", databaseConfig.getServerName()));
        configRef.add(new StringRefAddr("databaseName", databaseConfig.getDatabaseName()));
        configRef.add(new StringRefAddr("user", databaseConfig.getUser()));
        configRef.add(new StringRefAddr("password", databaseConfig.getPassword()));
        configRef.add(new StringRefAddr("serverTimezone", TimeZones.GMT_ID));
        configRef.add(new StringRefAddr("characterEncoding", "UTF-8"));
        dataSources = (MysqlDataSource) new MysqlDataSourceFactory()
                .getObjectInstance(configRef, null, null, null);
    }

    public static void initService(Config.DatabaseConfig databaseConfig) throws Exception {
        if (instance != null) {
            throw new RuntimeException("DatabaseAccessHolding service already initialized");
        }
        instance = new DatabaseAccessHolding(databaseConfig);
    }

    public static DatabaseAccessHolding getInstance() {
        if (instance == null) {
            throw new RuntimeException("DatabaseAccessHolding service has not been initialized");
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSources.getConnection();
    }

    public static ResultSet executeQuery(@NotNull Connection connection, @NotNull String sql) throws SQLException {
        return connection.createStatement().executeQuery(sql);
    }

    public static int executeUpdate(@NotNull Connection connection, @NotNull String sql) throws SQLException {
        return connection.createStatement().executeUpdate(sql);
    }
}
