package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotations.AddUserToDB;
import guru.qa.niffler.jupiter.annotations.Dao;
import guru.qa.niffler.jupiter.annotations.DeleteUserFromDB;
import guru.qa.niffler.jupiter.extensions.DaoExtension;
import guru.qa.niffler.pages.WelcomePage;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@ExtendWith(DaoExtension.class)
public class RegisterTests extends BaseWebTest {

    @Dao
    private AuthUserDAO authUserDAO;
    @Dao
    private UserDataUserDAO userDataUserDAO;

    @CsvSource(value = {"Ivan.Ivanovich, 12345"})
    @ParameterizedTest(name = "Register new user {0}")
    @AllureId("5")
    @DeleteUserFromDB(user = "Ivan.Ivanovich")
    void registerNewUserTest(String user, String pass) {
        Selenide.open(cfg.baseUrl(), WelcomePage.class)
                .toRegisterNewUser()
                .checkPageLoaded()
                .registerNewUser(user, pass)
                .login(user, pass)
                .checkPageLoaded();
    }


    @AddUserToDB
    @Test
    @AllureId("6")
    void registerExistingUser(AuthUserEntity user) {
        Selenide.open(cfg.baseUrl(), WelcomePage.class)
                .toRegisterNewUser()
                .checkPageLoaded()
                .registerExistUser(user.getUsername(), user.getPassword())
                .checkExistUser(user.getUsername());
    }
}
