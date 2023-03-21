/*
 * Copyright 2019-2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.yy;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import razesoldier.eqgbot.Config;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;

/**
 * 用于登录YY的帮助类
 */
public class YYLoginHelper {
    private YYLoginHelper() {}

    public static boolean validateCookie(String cookie) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create("https://channel.yy.com/channel/display!displayChannelInfo.action?sid=55328941"))
                .setHeader("Cookie", cookie).build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
    }

    @NotNull
    public static String attemptLoginWithSelenium(Config.YYLoginCredential yyLoginCredential) {
        ChromeDriver chromeDriver = new ChromeDriver(new ChromeOptions().addArguments("--headless", "--no-sandbox"));
        chromeDriver.get("https://channel.yy.com");
        chromeDriver.switchTo().frame("udbsdk_frm_normal");
        // 等待登录悬浮框弹出
        new WebDriverWait(chromeDriver, Duration.ofSeconds(5)).until(driver -> driver.findElement(By.className("m_commonLogin")));
        if (chromeDriver.findElement(By.className("qnotice")).getText().equals("检测到您登录的YY客户端或YY浏览器帐号")) {
            // 当网页检测到本机有YY客户端正在运行可能会显示提示
            // 使用xpath找到“使用账号密码登录”链接并点击
            chromeDriver.findElement(By.linkText("使用帐号密码登录")).click();
        }

        var accountInput = new WebDriverWait(chromeDriver, Duration.ofSeconds(10))
                .until(driver -> driver.findElement(By.className("E_acct")));
        try {
            // 休眠当前线程500ms，用来防止输入框不可交互
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new YYLoginRuntimeException(e);
        }
        accountInput.sendKeys(yyLoginCredential.getUsername());
        chromeDriver.findElement(By.className("E_passwd")).sendKeys(yyLoginCredential.getPassword() + "\n");
        // 显式等待YY欢迎页面，超时设置为10秒
        new WebDriverWait(chromeDriver, Duration.ofSeconds(10)).until(driver -> driver.getTitle().equals("YY公会管理 - 欢迎您"));
        var cookies = getCookieText(chromeDriver.manage().getCookies());
        chromeDriver.quit();
        return cookies;
    }

    @NotNull
    private static String getCookieText(@NotNull Set<Cookie> cookies) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Cookie cookie :
                cookies) {
            stringBuilder.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
        }
        return stringBuilder.toString();
    }
}
