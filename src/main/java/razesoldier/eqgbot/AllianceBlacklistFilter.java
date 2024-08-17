/*
 * Copyright 2019-2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot;

import org.intellij.lang.annotations.Language;
import razesoldier.eqgbot.dba.DatabaseAccessHolding;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AllianceBlacklistFilter {
    public boolean filter(int characterId) throws SQLException {
        @Language("MySQL") var sql = "select id from alliance_blacklists where banned_character_id=?;";
        try (PreparedStatement ps = DatabaseAccessHolding.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, characterId);
            return ps.executeQuery().next();
        }
    }
}
