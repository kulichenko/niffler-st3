package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.spend.CategoryEntity;
import guru.qa.niffler.jupiter.annotations.AddCategoryToDB;
import guru.qa.niffler.jupiter.annotations.AddSpendViaAPI;
import guru.qa.niffler.jupiter.annotations.AddUserToDB;
import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.Dao;
import guru.qa.niffler.jupiter.extensions.DaoExtension;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.pages.MainPage;
import guru.qa.niffler.pages.WelcomePage;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DaoExtension.class)
public class SpendTests extends BaseWebTest {
    @Dao
    private AuthUserDAO authUserDAO;
    @Dao
    private UserDataUserDAO userDataUserDAO;

    @AddUserToDB
    @AddCategoryToDB
    @Test
    @AllureId("3")
    void addAndCheckSpendInTableTest(AuthUserEntity user, CategoryEntity category) {

        SpendJson spend = new SpendJson();
        spend.setUsername(user.getUsername());
        spend.setDescription("Описание затраты");
        spend.setCategory(category.getCategory());
        spend.setAmount(10000.0);
        spend.setCurrency(CurrencyValues.RUB);
        Selenide.open(cfg.baseUrl(), WelcomePage.class)
                .checkPageLoaded()
                .goToLoginPage()
                .checkPageLoaded()
                .login(user.getUsername(), user.getPassword())
                .addNewSpend(spend)
                .checkSpendsInHistory(spend);
    }

    @AddUserToDB
    @AddCategoryToDB
    @AddSpendViaAPI
    @Test
    @AllureId("4")
    void deleteSpendTest(AuthUserEntity user, CategoryEntity category, SpendJson spend) {
        Selenide.open(cfg.baseUrl(), WelcomePage.class)
                .checkPageLoaded()
                .goToLoginPage()
                .checkPageLoaded()
                .login(user.getUsername(), user.getPassword())
                .deleteSpendInHistory(spend);
    }

    @AddUserToDB
    @ApiLogin()
    @AddCategoryToDB
    @AddSpendViaAPI
    @Test
    @AllureId("14")
    void apiLoginDeleteSpendTest(SpendJson spend) {
        Selenide.open(cfg.baseUrl() + MainPage.URL, MainPage.class)
                .checkPageLoaded()
                .deleteSpendInHistory(spend);
    }
}
