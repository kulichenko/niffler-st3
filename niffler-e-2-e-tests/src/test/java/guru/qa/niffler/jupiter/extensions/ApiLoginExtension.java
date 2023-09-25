package guru.qa.niffler.jupiter.extensions;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.AuthServiceClient;
import guru.qa.niffler.api.context.CookieContext;
import guru.qa.niffler.api.context.SessionStorageContext;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotations.AddUserToDB;
import guru.qa.niffler.jupiter.annotations.ApiLogin;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;

import java.io.IOException;

import static guru.qa.niffler.jupiter.extensions.DBUserExtension.AUTH_USER;
import static guru.qa.niffler.jupiter.extensions.DBUserExtension.NAMESPACE_USER;

public class ApiLoginExtension implements BeforeEachCallback, AfterTestExecutionCallback {

    private final AuthServiceClient authServiceClient = new AuthServiceClient();

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        ApiLogin apiLoginAnnotation = extensionContext.getRequiredTestMethod().getAnnotation(ApiLogin.class);
        AddUserToDB addUserToDbAnnotation = extensionContext.getRequiredTestMethod().getAnnotation(AddUserToDB.class);
        if (apiLoginAnnotation != null) {
            if (addUserToDbAnnotation != null) {
                AuthUserEntity authUserEntity = extensionContext.getStore(NAMESPACE_USER).get(AUTH_USER, AuthUserEntity.class);
                doLogin(authUserEntity.getUsername(), authUserEntity.getPassword());
            } else {
                doLogin(apiLoginAnnotation.username(), apiLoginAnnotation.password());
            }
        }

    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        SessionStorageContext.getInstance().clearContext();
    }

    private void doLogin(String username, String password) {
        SessionStorageContext sessionStorageContext = SessionStorageContext.getInstance();
        sessionStorageContext.init();

        try {
            authServiceClient.doLogin(username, password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Selenide.open(Config.getInstance().baseUrl());
        Selenide.sessionStorage().setItem("codeChallenge", sessionStorageContext.getCodeChallenge());
        Selenide.sessionStorage().setItem("id_token", sessionStorageContext.getToken());
        Selenide.sessionStorage().setItem("codeVerifier", sessionStorageContext.getCodeVerifier());
        Cookie jsessionIdCookie = new Cookie("JSESSIONID", CookieContext.getInstance().getJSessionIdCookieValue());
        WebDriverRunner.getWebDriver().manage().addCookie(jsessionIdCookie);
    }
}
