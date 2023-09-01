package guru.qa.niffler.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import guru.qa.niffler.jupiter.annotations.Dao;
import guru.qa.niffler.jupiter.extensions.DaoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(DaoExtension.class)
public class DBUserTests extends BaseWebTest {

    @Dao
    private AuthUserDAO authUserDAO;
    @Dao
    private UserDataUserDAO userDataUserDAO;

    @DBUser()
    @Test
    void mainPageShouldBeVisibleAfterLogin(AuthUserEntity user) {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(user.getUsername());
        $("input[name='password']").setValue(user.getPassword());
        $("button[type='submit']").click();
        $(".main-content__section-stats").should(Condition.visible);
    }

    @DBUser()
    @Test
    void selectUserFromDbTest(AuthUserEntity user) {
        var userFromDb = authUserDAO.getUserById(user.getId());
        assertAll(
                () -> assertEquals(user.getUsername(), userFromDb.getUsername(), "usernames are not equals"),
                () -> assertEquals(user.getAccountNonExpired(), userFromDb.getAccountNonExpired(), "AccountNonExpired are not equals"),
                () -> assertEquals(user.getAuthorities(), userFromDb.getAuthorities(), "authorities are not equals")
                //etc
        );
    }

    @DBUser()
    @Test
    void updateUserTest(AuthUserEntity user) {
//        var userFromDb = authUserDAO.getUserById(user.getId());
//        assertTrue(userFromDb.getAccountNonLocked(), "account locked");
        user.setAccountNonLocked(false);
        AuthUserEntity authUserEntity = authUserDAO.updateUser(user);
        assertFalse(authUserEntity.getAccountNonLocked(), "account not locked");
    }
}