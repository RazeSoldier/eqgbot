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
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;

@Log
public class AssignAdminAction {
    private final YYClient yyClient;

    public AssignAdminAction(YYClient yyClient) {
        this.yyClient = yyClient;
    }

    public YYOperationResult handle(@NotNull YYAccount yyAccount) throws YYOperationException {
        if (yyClient.isLogoff()) {
            log.warning("Not login to YY");
            return YYOperationResult.newFail(yyAccount, "Not login to YY");
        }
        HttpResponse<String> resp = assignAdminRole(yyAccount);
        if (resp.statusCode() == 200 && resetAdminPermissions(yyAccount).statusCode() == 200) {
            var msg = String.format("成功将`%s`变成黄马", yyAccount.getName());
            log.info(msg);
            return YYOperationResult.newSuccess(yyAccount, msg);
        } else {
            var msg = String.format("未能将`%s`变成黄马", yyAccount.getName());
            log.warning(msg);
            return YYOperationResult.newFail(yyAccount, msg);
        }
    }

    private HttpResponse<String> assignAdminRole(@NotNull YYAccount yyAccount) throws YYOperationException {
        var url = String.format("https://channel.yy.com/ajax/channel/members/operation!changeMemberRole.action?sid=55328941&uids=%d&newrole=ma&data=p-ma-200&vrp=p&dialogOpts=%%5Bobject+Object%%5D&token=undefined", yyAccount.getUid());
        return yyClient.yyOperate(url);
    }

    private HttpResponse<String> resetAdminPermissions(@NotNull YYAccount yyAccount) throws YYOperationException {
        var url = String.format("https://channel.yy.com/ajax/channel/rightsList/operation!resetChannelMemberRights.action?sid=55328941&uid=%d&roleType=roleMA", yyAccount.getUid());
        return yyClient.yyOperate(url);
    }
}
