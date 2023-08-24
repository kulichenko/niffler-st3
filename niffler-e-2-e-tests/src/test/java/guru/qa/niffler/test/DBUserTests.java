package guru.qa.niffler.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.dao.AuthUserDAOJdbc;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.DBUser;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DBUserTests extends BaseWebTest {

    @DBUser(username = "user_01", password = "12345")
    @Test
    void mainPageShouldBeVisibleAfterLogin(UserEntity user) {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(user.getUsername());
        $("input[name='password']").setValue(user.getPassword());
        $("button[type='submit']").click();
        $(".main-content__section-stats").should(Condition.visible);
    }

    @DBUser(username = "user_02", password = "12345")
    @Test
    void selectUserFromDbTest(UserEntity user) {
        var authUserDAOJdbc = new AuthUserDAOJdbc();
        var userFromDb = authUserDAOJdbc.getUserFromDBById(user.getId());
        assertAll(
                () -> assertEquals(user.getUsername(), userFromDb.getUsername(), "usernames are not equals"),
                () -> assertEquals(user.getAccountNonExpired(), userFromDb.getAccountNonExpired(), "AccountNonExpired are not equals"),
                () -> assertEquals(user.getAuthorities(), userFromDb.getAuthorities(), "authorities are not equals")
                //etc
        );
    }

    @DBUser(username = "user_03", password = "12345")
    @Test
    void updateUserTest(UserEntity user) {
        var authUserDAOJdbc = new AuthUserDAOJdbc();
        var userFromDb = authUserDAOJdbc.getUserFromDBById(user.getId());
        assertTrue(userFromDb.getAccountNonLocked(), "account locked");
        userFromDb.setAccountNonLocked(false);
        authUserDAOJdbc.updateUser(userFromDb);
        assertFalse(userFromDb.getAccountNonLocked(), "account not  locked");
    }
}