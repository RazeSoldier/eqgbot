/*
 * Copyright 2019-2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.yy;

import lombok.extern.java.Log;

@Log
public class RevokeAdminAction {
    private final YYClient yyClient;

    public RevokeAdminAction(YYClient yyClient) {
        this.yyClient = yyClient;
    }

    public YYOperationResult handle(YYAccount yyAccount) throws YYOperationException {
        if (yyClient.isLogoff()) {
            log.warning("Not login to YY");
            return YYOperationResult.newFail(yyAccount, "Not login to YY");
        }

        var url = String.format("https://channel.yy.com/ajax/channel/members/operation!changeMemberRole.action?sid=55328941&uids=%d&newrole=member&data=r-ma-200&vrp=r&dialogOpts=%%5Bobject+Object%%5D&token=undefined", yyAccount.getUid());
        var resp = yyClient.yyOperate(url);
        if (resp.statusCode() == 200) {
            var msg = String.format("成功移除`%s`的黄马", yyAccount.getName());
            log.info(msg);
            return YYOperationResult.newSuccess(yyAccount, msg);
        } else {
            var msg = String.format("未能移除`%s`的黄马", yyAccount.getName());
            log.warning(msg);
            return YYOperationResult.newFail(yyAccount, msg);
        }
    }
}
