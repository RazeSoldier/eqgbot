/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import razesoldier.eqgbot.dba.DatabaseAccessHolding;
import razesoldier.eqgbot.dba.SQLExecuteException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EVEUser {
    private final long qq;
    private final String name;
    private final String corpName;
    private final String allianceName;
    private final Integer id;

    private EVEUser(long qq, @NotNull String name, @NotNull String corpName, @Nullable String allianceName, Integer id) {
        this.qq = qq;
        this.name = name;
        this.corpName = corpName;
        this.allianceName = allianceName;
        this.id = id;
    }

    public static EVEUser newInstance(long id, GameServer server) throws SQLExecuteException {
        if (server == GameServer.OF) {
            return EVEUser.newInstanceFromOF(id);
        }
        if (server == GameServer.GF) {
            return EVEUser.newInstanceFromGF(id);
        }
        throw new RuntimeException("Unknown server: " + server);
    }

    @Nullable
    public static EVEUser newInstanceFromOF(long id) throws SQLExecuteException {
        int uId;
        String name;
        String corpName;
        String allianceName;
        try (Connection connection = getConnection(GameServer.OF)) {
            ResultSet set = queryFirst(
                    connection,
                    "select users.id,qq.name,qq.corp,qq.alliance from qq,users where qq.name=users.name and qq.qq=" + id
            );
            if (set == null) return null;
            uId = set.getInt("id");
            name = set.getString("name");
            corpName = set.getString("corp");
            allianceName = set.getString("alliance");
        } catch (SQLException throwables) {
            throw new SQLExecuteException(throwables);
        }
        return new EVEUser(id, name, corpName, allianceName, uId);
    }

    @Nullable
    public static EVEUser newInstanceFromGF(long id) throws SQLExecuteException {
        try (Connection conn = getConnection(GameServer.GF)) {
            int uId;
            String name;
            String corpName;
            String allianceName = null;
            int corpId;
            int allianceId;
            {
                var sql = "select characters.id,characters.name,characters.corporation_id,characters.alliance_id " +
                        "from qqs,users,characters " +
                        "where qqs.user_id=users.id and users.id=characters.id and qqs.qq_id=" + id;
                var set = queryFirst(conn, sql);
                if (set == null) return null;
                uId = set.getInt("id");
                name = set.getString("name");
                corpId = set.getInt("corporation_id");
                allianceId = set.getInt("alliance_id");
            }
            {
                ResultSet set = queryFirst(conn, "select name from corporations where id=" + corpId);
                if (set == null) return null;
                corpName = set.getString("name");
            }
            if (allianceId != 0) {
                var set = DatabaseAccessHolding.executeQuery(
                        conn, "select name from alliances where id=" + allianceId
                );
                if (set.next()) {
                    allianceName = set.getString("name");
                }
            }
            return new EVEUser(id, name, corpName, allianceName, uId);
        } catch (SQLException throwables) {
            throw new SQLExecuteException(throwables);
        }
    }

    private static Connection getConnection(GameServer servers) throws SQLException {
        return DatabaseAccessHolding.getInstance().getConnection(servers);
    }

    @Nullable
    private static ResultSet queryFirst(@NotNull Connection connection, @NotNull String sql) throws SQLException {
        var set = DatabaseAccessHolding.executeQuery(
                connection, sql
        );
        if (!set.next()) {
            return null;
        }
        return set;
    }

    public long getQq() {
        return qq;
    }

    public String getName() {
        return name;
    }

    public String getCorpName() {
        return corpName;
    }

    public String getAllianceName() {
        return allianceName;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "EVEUser{" +
                "qq=" + qq +
                ", name='" + name + '\'' +
                ", corpName='" + corpName + '\'' +
                ", allianceName='" + allianceName + '\'' +
                '}';
    }
}
