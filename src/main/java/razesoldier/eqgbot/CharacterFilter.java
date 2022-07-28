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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.dba.SQLExecuteException;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CharacterFilter {
    private final List<EVECharacter> characters;

    @NotNull
    @Contract("_ -> new")
    public static CharacterFilter of(@NotNull EVEUser eveUser) throws SQLExecuteException {
        return new CharacterFilter(eveUser.getCharacters());
    }

    @NotNull
    @Contract("_ -> new")
    public static CharacterFilter of(@NotNull List<EVEUser> users) throws SQLExecuteException {
        List<EVECharacter> characters = new ArrayList<>();
        for (EVEUser user: users) {
            characters.addAll(user.getCharacters());
        }
        return new CharacterFilter(characters);
    }

    /**
     * 按照联盟ID过滤
     */
    public List<EVECharacter> filterAlliance(int allianceId) {
        return characters.stream().filter(c -> c.getAllianceId() == allianceId).toList();
    }
}
