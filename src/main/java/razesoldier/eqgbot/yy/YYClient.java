/*
 * Copyright 2019-2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.yy;

import com.alibaba.fastjson2.JSON;
import lombok.extern.java.Log;
import razesoldier.eqgbot.Config;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class YYClient {
    private final CookieCache cookieCache;
    private final Config.YYLoginCredential yyLoginCredential;
    private String cookies;

    public YYClient(Config.YYLoginCredential yyLoginCredential) {
        log.setLevel(Level.FINE);
        log.getParent().getHandlers()[0].setLevel(Level.FINE);
        this.yyLoginCredential = yyLoginCredential;
        cookieCache = CookieCache.getInstance();
    }

    public void attemptLogin() throws LoginException {
        if (cookieCache.exists()) {
            log.info("[YYClient::attemptLogin] YY cookie cache exists");
            try {
                if (YYLoginHelper.validateCookie(cookieCache.getContent())) {
                    log.info("[YYClient::attemptLogin] Use YY cookie cache to login success");
                    cookies = cookieCache.getContent();
                    return;
                }
                log.info("[YYClient::attemptLogin] Use YY cookie cache to login failed");
            } catch (IOException e) {
                throw new LoginException(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new LoginException(e);
            }
        }

        log.info("[YYClient::attemptLogin] Try login with selenium");
        cookies = YYLoginHelper.attemptLoginWithSelenium(yyLoginCredential);

        try {
            cookieCache.save(cookies);
            log.info("Saved YY cookie");
        } catch (IOException e) {
            throw new LoginException(e);
        }
    }

    public Optional<YYAccount> getAccountByYYId(Long yyId) throws YYOperationException {
        try {
            attemptLogin();
        } catch (LoginException e) {
            throw new YYOperationException(e);
        }
        var url = String.format("https://channel.yy.com/ajax/channel/members/operation!queryMembersOfChannel.action?sid=55328941&roleName=total&imid=%d&nickName=", yyId);
        var request = HttpRequest.newBuilder(URI.create(url)).setHeader("Cookie", cookies).build();
        HttpResponse<String> resp = sendHttpRequest(request);
        log.fine(String.format("[YYClient::getAccountByYYId] Request account data, argument {yyId: %d}, resp code: %d, resp body: %s", yyId, resp.statusCode(), resp.body()));
        var array = JSON.parseObject(resp.body()).getJSONObject("data").getJSONObject("pageInfo").getJSONArray("records");
        if (array.isEmpty()) {
            return Optional.empty();
        }
        var accountData = array.getJSONObject(0);
        var account = new YYAccount(yyId, accountData.getLong("uid"), accountData.getString("nickName"));
        return Optional.of(account);
    }

    HttpResponse<String> yyOperate(String url) throws YYOperationException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(url))
                .setHeader("Cookie", cookies)
                .build();
        return sendHttpRequest(request);
    }

    public boolean isLogoff() {
        return cookies == null;
    }

    private static HttpResponse<String> sendHttpRequest(HttpRequest request) throws YYOperationException {
        HttpResponse<String> resp;
        try {
            resp = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new YYOperationException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new YYOperationException(e);
        }
        return resp;
    }
}
