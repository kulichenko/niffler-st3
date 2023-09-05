package guru.qa.niffler.test.uitests;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import guru.qa.niffler.jupiter.annotations.Dao;
import guru.qa.niffler.jupiter.extensions.DaoExtension;
import guru.qa.niffler.pages.WelcomePage;
import guru.qa.niffler.test.BaseWebTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DaoExtension.class)
public class LoginTest extends BaseWebTest {
    @Dao
    private AuthUserDAO authUserDAO;
    @Dao
    private UserDataUserDAO userDataUserDAO;

    @DBUser
    @Test
    void loginTest(AuthUserEntity user) {
        Selenide.open("http://127.0.0.1:3000", WelcomePage.class)
                .checkPageLoaded()
                .goToLoginPage()
                .checkPageLoaded()
                .login(user.getUsername(), user.getPassword())
                .checkPageLoaded();
    }


    @DBUser
    @Test
    void incorrectUserLoginTest(AuthUserEntity user) {
        Selenide.open("http://127.0.0.1:3000", WelcomePage.class)
                .checkPageLoaded()
                .goToLoginPage()
                .checkPageLoaded()
                .wrongUsernameLogin(user.getUsername(), user.getPassword());
    }
}
