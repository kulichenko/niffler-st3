package guru.qa.niffler.test.web;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.Friend;
import guru.qa.niffler.jupiter.annotations.GenerateUser;
import guru.qa.niffler.jupiter.annotations.GeneratedUser;
import guru.qa.niffler.jupiter.annotations.IncomeInvitation;
import guru.qa.niffler.jupiter.annotations.OutcomeInvitation;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pages.FriendsPage;
import guru.qa.niffler.pages.PeoplePage;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class FriendsWebTest extends BaseWebTest {

    @Test
    @AllureId("15")
    @DisplayName("Друзья должны быть видны на странице Friends ")
    @ApiLogin(
            user = @GenerateUser(
                    friends = @Friend(count = 2))
    )
    void friendSholdBeVisibleOnFriendPage(@GeneratedUser UserJson userForTest) {
        open(cfg.baseUrl() + FriendsPage.URL, FriendsPage.class)
                .checkPageContent();
        $(".main-content__section")
                .$("tbody")
                .$$("td")
                .find(Condition.text("You are friends"));
    }

    @Test
    @AllureId("16")
    @DisplayName("Приглашения дружить должны быть видны на странице All people ")
    @ApiLogin(
            user = @GenerateUser(
                    incomeInvitations = @IncomeInvitation(count = 2))
    )
    void incomeInvitationSholdBeVisibleOnPeoplePage(@GeneratedUser UserJson userForTest) {
        open(cfg.baseUrl() + PeoplePage.URL, PeoplePage.class)
                .checkPageContent();
        $(".main-content__section")
                .$("tbody")
                .$$("[data-tooltip-content='Submit invitation']")
                .shouldBe(CollectionCondition.size(2));
    }

    @Test
    @AllureId("17")
    @DisplayName("Приглашения дружить должны быть видны на странице Friends ")
    @ApiLogin(
            user = @GenerateUser(
                    incomeInvitations = @IncomeInvitation(count = 2))
    )
    void incomeInvitationSholdBeVisibleOnFriendPage(@GeneratedUser UserJson userForTest) {
        open(cfg.baseUrl() + FriendsPage.URL, FriendsPage.class)
                .checkPageContent();
        $(".main-content__section")
                .$("tbody")
                .$$("[data-tooltip-content='Submit invitation']")
                .shouldBe(CollectionCondition.size(2));
    }

    @Test
    @AllureId("18")
    @DisplayName("Отправленные приглашения должны быть видны на странице People ")
    @ApiLogin(
            user = @GenerateUser(
                    outcomeInvitations = @OutcomeInvitation)
    )
    void outcomeInvitationSholdBeVisibleOnPeoplePage(@GeneratedUser UserJson userForTest) {
        open(cfg.baseUrl() + PeoplePage.URL, PeoplePage.class)
                .checkPageContent();
        $(".main-content__section")
                .$("tbody")
                .$$("td")
                .find(Condition.text("Pending invitation"));
    }


}
