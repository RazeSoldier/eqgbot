/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.esi;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 代表角色认证的信息。
 */
public class CharacterAuthentication {
    private final int characterId;
    private final List<String> scopes;
    private final String token;

    private CharacterAuthentication(int characterId, List<String> scopes, String token) {
        this.characterId = characterId;
        this.scopes = scopes;
        this.token = token;
    }

    @NotNull
    @Contract(value = "_, _, _ -> new", pure = true)
    public static CharacterAuthentication of(int characterId, List<String> scopes, String token) {
        return new CharacterAuthentication(characterId, scopes, token);
    }

    public int getCharacterId() {
        return characterId;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public String getToken() {
        return token;
    }
}
