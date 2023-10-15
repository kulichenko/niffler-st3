package guru.qa.niffler.test.ignored;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.model.userdata.UserDataUserEntity;
import guru.qa.niffler.jupiter.annotations.Dao;
import guru.qa.niffler.jupiter.extensions.DaoExtension;
import guru.qa.niffler.test.web.BaseWebTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static com.codeborne.selenide.Selenide.$;

@ExtendWith(DaoExtension.class)
public class LoginTest extends BaseWebTest {

    @Dao
    private AuthUserDAO authUserDAO;
    @Dao
    private UserDataUserDAO userDataUserDAO;

    private AuthUserEntity authUser;
    private UserDataUserEntity userDataUser;

    @BeforeEach
    void createUser() {
        authUser = new AuthUserEntity();
        authUser.setUsername("pavlik");
        authUser.setPassword("12345");
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(Arrays.stream(Authority.values())
                .map(authority -> {
                    var ae = new AuthorityEntity();
                    ae.setAuthority(authority);
                    return ae;
                }).toList()
        );
        authUserDAO.createUser(authUser);
        userDataUserDAO.createUserInUserData(userDataUser);
    }

    @AfterEach
    void deleteUser() {
        userDataUserDAO.deleteUserByUsernameInUserData(authUser.getUsername());
        authUserDAO.deleteUser(authUser);
    }

    @Disabled
    @Test
    void mainPageShouldBeVisibleAfterLogin() {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(authUser.getUsername());
        $("input[name='password']").setValue(authUser.getPassword());
        $("button[type='submit']").click();
        $(".main-content__section-stats").should(Condition.visible);
    }
}
