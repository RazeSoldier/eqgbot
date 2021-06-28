/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot;

/**
 * 代表游戏服务器的枚举。包括宁静（欧服）、晨曦（国服）
 */
public enum GameServer {
    GF(1, "晨曦"), OF(0, "宁静");

    private final int i;
    private final String name;

    GameServer(int i, String name) {
        this.i = i;
        this.name = name;
    }

    public int getI() {
        return i;
    }

    @Override
    public String toString() {
        return name;
    }
}
