/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import razesoldier.eqgbot.dba.DatabaseAccessHolding;
import razesoldier.eqgbot.dba.SQLExecuteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    @Nullable
    public static EVEUser newInstance(long id, Integer allianceIdFilter) throws SQLExecuteException {
        try (Connection conn = getConnection(GameServer.GF)) {
            int uId = 0;
            String name = null;
            String corpName;
            String allianceName = null;
            int corpId = 0;
            int allianceId = 0;
            {
                // 首先在`qqs`表查询所有与id一样的记录（可能不止一条因为很可能多个用户绑定同一个QQ号）
                @Language("MySQL") var preSql = """
                        select characters.id, characters.name, character_affiliations.corporation_id, character_affiliations.alliance_id
                        from qqs
                            inner join users on users.id = qqs.user_id
                            inner join user_characters uc on users.id = uc.user_id
                            inner join characters on characters.id = uc.character_id
                            inner join character_affiliations on character_affiliations.character_id = characters.id
                        where qq_id = ?
                        """;
                PreparedStatement stat = conn.prepareStatement(preSql);
                stat.setLong(1, id);
                if (allianceIdFilter == null) {
                    var set = stat.executeQuery();
                    if (!set.next()) return null;
                    uId = set.getInt("id");
                    name = set.getString("name");
                    corpId = set.getInt("corporation_id");
                    allianceId = set.getInt("alliance_id");
                } else {
                    // 如果设置了allianceIdFilter则遍历上面查询到的记录
                    // 对比角色的联盟ID和allianceIdFilter
                    var set = stat.executeQuery();
                    while (set.next()) {
                        if (set.getInt("alliance_id") == allianceIdFilter) {
                            uId = set.getInt("id");
                            name = set.getString("name");
                            corpId = set.getInt("corporation_id");
                            allianceId = set.getInt("alliance_id");
                        }
                    }
                    // 如果在这里uId / name / corpId 都为空说明该QQ号绑定的用户里没有符合allianceIdFilter的用户
                    if (uId == 0 || name == null || corpId == 0) {
                        return null;
                    }
                }
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
