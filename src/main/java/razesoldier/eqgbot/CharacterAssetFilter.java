/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */

package razesoldier.eqgbot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.dba.DatabaseAccessHolding;
import razesoldier.eqgbot.dba.SQLExecuteException;

import java.sql.*;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CharacterAssetFilter {
    private final List<EVECharacter> characters;

    @NotNull
    @Contract("_ -> new")
    public static CharacterAssetFilter of(List<EVECharacter> list) {
        return new CharacterAssetFilter(list);
    }

    /**
     * 判断角色列表包括的角色是否有泰坦资产
     */
    public boolean hasTitan() throws SQLExecuteException {
        List<Integer> ids = characters.stream().map(EVECharacter::getId).toList();
        try (Connection conn = DatabaseAccessHolding.getInstance().getConnection()) {
            return queryDatabase(ids, conn);
        } catch (SQLException e) {
            throw new SQLExecuteException(e);
        }
    }

    private static boolean queryDatabase(List<Integer> ids, Connection conn) throws SQLExecuteException {
        @Language("MySQL") var sql = """
                select * from titan_pilots
                where character_id in (?)
                """;
        sql = sql.replace("?", StringUtils.join(ids, ","));
        try (Statement stat = conn.createStatement()) {
            ResultSet res = stat.executeQuery(sql);
            return res.next();
        } catch (SQLException e) {
            throw new SQLExecuteException(e);
        }
    }
}
