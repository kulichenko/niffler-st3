package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotations.AddUserToDB;
import guru.qa.niffler.jupiter.annotations.Dao;
import guru.qa.niffler.jupiter.extensions.DaoExtension;
import guru.qa.niffler.pages.WelcomePage;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DaoExtension.class)
public class LoginTest extends BaseWebTest {
    @Dao
    private AuthUserDAO authUserDAO;
    @Dao
    private UserDataUserDAO userDataUserDAO;

    @AddUserToDB
    @Test
    @AllureId("1")
    void loginTest(AuthUserEntity user) {
        Selenide.open(cfg.baseUrl(), WelcomePage.class)
                .checkPageLoaded()
                .goToLoginPage()
                .checkPageLoaded()
                .login(user.getUsername(), user.getPassword())
                .checkPageLoaded();
    }


    @AddUserToDB
    @Test
    @AllureId("2")
    void incorrectUserLoginTest(AuthUserEntity user) {
        Selenide.open(cfg.baseUrl(), WelcomePage.class)
                .checkPageLoaded()
                .goToLoginPage()
                .checkPageLoaded()
                .wrongUsernameLogin(user.getUsername(), user.getPassword());
    }
}
