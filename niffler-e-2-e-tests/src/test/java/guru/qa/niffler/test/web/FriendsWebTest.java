package guru.qa.niffler.test.web;

import com.codeborne.selenide.Condition;
import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.Friend;
import guru.qa.niffler.jupiter.annotations.GenerateUser;
import guru.qa.niffler.jupiter.annotations.GeneratedUser;
import guru.qa.niffler.jupiter.annotations.IncomeInvitation;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.jupiter.annotations.GeneratedUser.Selector.NESTED;
import static guru.qa.niffler.jupiter.annotations.GeneratedUser.Selector.OUTER;

public class FriendsWebTest extends BaseWebTest {
//    @BeforeEach
//    void doLogin(@User(userType = WITH_FRIENDS) UserJson userForTest) {
//        Selenide.open("http://127.0.0.1:3000/main");
//        $("a[href*='redirect']").click();
//        $("input[name='username']").setValue(userForTest.getUsername());
//        $("input[name='password']").setValue(userForTest.getPassword());
//        $("button[type='submit']").click();
//    }


    @Test
    @AllureId("10")
    void friendsShouldBeVisibleInTable() throws InterruptedException {
        $("li[data-tooltip-id=friends]").click();
        $(".main-content__section").$("tbody").$$("td")
                .findBy(Condition.text("You are friends")).shouldBe(Condition.visible);
    }

    @ApiLogin(
            user = @GenerateUser(
                    friends = @Friend,
                    incomeInvitations = @IncomeInvitation
            )
    )
    @GenerateUser
    @Test
    @AllureId("21324")
    void incomeInvitationShouldBePresentInTable(@GeneratedUser(selector = NESTED) UserJson userForTest,
                                                @GeneratedUser(selector = OUTER) UserJson another) {
        open(cfg.baseUrl() + "/main");
        System.out.println();
    }
}
