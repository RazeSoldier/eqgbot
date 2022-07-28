/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import razesoldier.eqgbot.dba.DatabaseAccessHolding;
import razesoldier.eqgbot.dba.SQLExecuteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class EVEUser {
    private final int id;
    private final long qq;
    private final int mainCharacterId;
    private List<EVECharacter> characters;

    /**
     * 给定QQ号，在EIMS数据库搜索相关的用户
     * @param qq QQ号
     * @return 返回和qq绑定的用户的列表。如果没有匹配的用户则会返回NULL
     */
    public static Optional<List<EVEUser>> newInstance(long qq) throws SQLExecuteException {
        try (Connection conn = getConnection()) {
            @Language("MySQL") var preSql = """
                    select u.* from qqs
                    inner join users u on qqs.user_id = u.id
                    where qq_id = ?
                    """;
            try (PreparedStatement preStat = conn.prepareStatement(preSql)) {
                preStat.setLong(1, qq);
                ResultSet res = preStat.executeQuery();
                List<EVEUser> users = new ArrayList<>();
                while (res.next()) {
                    users.add(new EVEUser(res.getInt("id"), qq, res.getInt("main_character_id")));
                }
                if (users.isEmpty()) {
                    // 如果没有结果则直接返回NULL
                    return Optional.empty();
                }
                return Optional.of(users);
            }

        } catch (SQLException e) {
            throw new SQLExecuteException(e);
        }
    }

    /**
     * 获得这个用户下的所有已绑定的角色列表
     */
    public List<EVECharacter> getCharacters() throws SQLExecuteException {
        if (characters != null) {
            return characters;
        }
        List<EVECharacter> list = new ArrayList<>();
        try (Connection conn = getConnection()) {
            @Language("MySQL") var preSql = """
                    select c.id, c.name, ca.corporation_id, c2.name as 'corp_name', ca.alliance_id, c.name as 'alliance_name'
                    from user_characters
                    inner join characters c on c.id = user_characters.character_id
                    inner join character_affiliations ca on c.id = ca.character_id
                    inner join corporations c2 on ca.corporation_id = c2.id
                    left join alliances a on ca.alliance_id = a.id
                    where user_id = ?
                    """;
            try (PreparedStatement preparedStatement = conn.prepareStatement(preSql)) {
                preparedStatement.setInt(1, id);
                ResultSet res = preparedStatement.executeQuery();
                while (res.next()) {
                    Integer allianceId = res.getInt("alliance_id") == 0 ? res.getInt("alliance_id") : null;
                    list.add(
                            new EVECharacter(this,
                                    res.getInt("id"),
                                    res.getString("name"),
                                    res.getInt("corporation_id"),
                                    res.getString("corp_name"),
                                    allianceId,
                                    res.getString("alliance_name")
                            )
                    );
                }
            }
        } catch (SQLException e) {
            throw new SQLExecuteException(e);
        }
        characters = list;
        return characters;
    }

    public EVECharacter getMainCharacter() throws SQLExecuteException {
        if (characters == null) {
            getCharacters();
        }
        for (EVECharacter character: characters) {
            if (character.getId() == mainCharacterId) {
                return character;
            }
        }
        throw new RuntimeException("Unable find main character from user: " + id);
    }

    private static Connection getConnection() throws SQLException {
        return DatabaseAccessHolding.getInstance().getConnection(GameServer.GF);
    }
}
