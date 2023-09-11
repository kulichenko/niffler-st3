package guru.qa.niffler.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;

public class InvitationWebTest extends BaseWebTest {
    @BeforeEach
    void doLogin(@User(userType = User.UserType.INVITATION_SENT) UserJson userJson) {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(userJson.getUsername());
        $("input[name='password']").setValue(userJson.getPassword());
        $("button[type='submit']").click();
    }

    @Test
    @AllureId("102")
    @DisplayName("Проверка наличия статуса 'Pending invitation' на странице '/people'")
    void checkInvitationOnPeoplePageTest() {
        $("li[data-tooltip-id=people]").click();
        $(".main-content__section").$("tbody").$$("td")
                .findBy(Condition.text("Pending invitation")).shouldBe(Condition.visible);
    }

}
