/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.dba;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.jdbc.MysqlDataSourceFactory;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.Config;
import razesoldier.eqgbot.GameServer;

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
    private final DataSource[] dataSources = new DataSource[2];

    private DatabaseAccessHolding(Config.DatabaseConfig ofConfig, Config.DatabaseConfig gfConfig) throws Exception {
        final var className = MysqlDataSource.class.getName();

        var ofConfigRef = new Reference(className);
        ofConfigRef.add(new StringRefAddr("serverName", ofConfig.getServerName()));
        ofConfigRef.add(new StringRefAddr("databaseName", ofConfig.getDatabaseName()));
        ofConfigRef.add(new StringRefAddr("user", ofConfig.getUser()));
        ofConfigRef.add(new StringRefAddr("password", ofConfig.getPassword()));
        dataSources[GameServer.OF.getI()] = (MysqlDataSource) new MysqlDataSourceFactory()
                .getObjectInstance(ofConfigRef, null, null, null);

        var gfConfigRef = new Reference(className);
        gfConfigRef.add(new StringRefAddr("serverName", gfConfig.getServerName()));
        gfConfigRef.add(new StringRefAddr("databaseName", gfConfig.getDatabaseName()));
        gfConfigRef.add(new StringRefAddr("user", gfConfig.getUser()));
        gfConfigRef.add(new StringRefAddr("password", gfConfig.getPassword()));
        dataSources[GameServer.GF.getI()] = (MysqlDataSource) new MysqlDataSourceFactory()
                .getObjectInstance(gfConfigRef, null, null, null);
    }

    public static void initService(Config.DatabaseConfig ofConfig, Config.DatabaseConfig gfConfig) throws Exception {
        if (instance != null) {
            throw new RuntimeException("DatabaseAccessHolding service already initialized");
        }
        instance = new DatabaseAccessHolding(ofConfig, gfConfig);
    }

    public static DatabaseAccessHolding getInstance() {
        if (instance == null) {
            throw new RuntimeException("DatabaseAccessHolding service has not been initialized");
        }
        return instance;
    }

    public Connection getConnection(@NotNull GameServer connType) throws SQLException {
        return dataSources[connType.getI()].getConnection();
    }

    public static ResultSet executeQuery(@NotNull Connection connection, @NotNull String sql) throws SQLException {
        return connection.createStatement().executeQuery(sql);
    }

    public static int executeUpdate(@NotNull Connection connection, @NotNull String sql) throws SQLException {
        return connection.createStatement().executeUpdate(sql);
    }
}
