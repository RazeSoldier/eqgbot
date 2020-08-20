/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.feature;

abstract class FeatureBase implements Feature {
    protected Boolean isEnabled = false;
    abstract void handle();

    @Override
    public final void call() {
        if (isEnabled) {
            handle();
        }
    }

    public final void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }
}
