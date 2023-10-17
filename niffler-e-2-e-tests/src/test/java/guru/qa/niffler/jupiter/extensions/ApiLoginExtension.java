package guru.qa.niffler.jupiter.extensions;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.AuthServiceClient;
import guru.qa.niffler.api.context.CookieContext;
import guru.qa.niffler.api.context.SessionStorageContext;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.GenerateUser;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;

import java.io.IOException;

import static guru.qa.niffler.jupiter.extensions.CreateUserExtension.NESTED;

public class ApiLoginExtension implements BeforeEachCallback, AfterTestExecutionCallback {

    private final AuthServiceClient authServiceClient = new AuthServiceClient();


    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        ApiLogin annotation = extensionContext.getRequiredTestMethod().getAnnotation(ApiLogin.class);
        if (annotation != null) {
            GenerateUser user = annotation.user();
            if (user.handleAnnotation()) {
                UserJson createdUser = extensionContext.getStore(NESTED).get(
                        getAllureId(extensionContext),
                        UserJson.class
                );
                doLogin(createdUser.getUsername(), createdUser.getPassword());
            } else {
                doLogin(annotation.username(), annotation.password());
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

    private String getAllureId(ExtensionContext context) {
        var allureId = context.getRequiredTestMethod().getAnnotation(AllureId.class);
        if (allureId == null) {
            throw new IllegalStateException("Annotation @AllureId must be present!");
        }
        return allureId.value();
    }
}


//    Old Impl
//    @Override
//    public void beforeEach(ExtensionContext extensionContext) throws Exception {
//        ApiLogin apiLoginAnnotation = extensionContext.getRequiredTestMethod().getAnnotation(ApiLogin.class);
//        AddUserToDB addUserToDbAnnotation = extensionContext.getRequiredTestMethod().getAnnotation(AddUserToDB.class);
//        String username, password;
//        if (addUserToDbAnnotation != null) {
//            AuthUserEntity authUserEntity = extensionContext.getStore(NAMESPACE_USER).get(AUTH_USER, AuthUserEntity.class);
//            username = authUserEntity.getUsername();
//            password = authUserEntity.getPassword();
//        } else {
//            username = apiLoginAnnotation.username();
//            password = apiLoginAnnotation.password();
//        }
//        doLogin(username, password);
//    }