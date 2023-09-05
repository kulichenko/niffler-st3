package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends NifflerBasePage {

    //Statistic area
    private final SelenideElement statisticArea = $(".main-content__section-stats");    //Add new spending area
    private final SelenideElement
            addNewSpendingArea = $(".main-content__section-add-spending"),
            selectSpendingCategoryDropDown = addNewSpendingArea.$(".select-wrapper"),
            setAmountInput = $("input[name='amount']"),
            spendDateCalendar = $(".calendar-wrapper "),
            spendingDescriptionInput = $("input[name='description']"),
            addNewSpendingButton = $("button[type='submit']");
    //History of spendings area
    private final SelenideElement historyArea = $(".main-content__section-history");

    @Override
    public <T extends NifflerBasePage> T checkPageContent() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Step("Check MainPage loaded")
    public MainPage checkPageLoaded() {
        addNewSpendingArea.shouldBe(visible);
        statisticArea.shouldBe(visible);
        historyArea.shouldBe(visible);
        addNewSpendingButton.shouldBe(interactable);
        return this;
    }

    @Step("Add new spend")
    public MainPage addNewSpend(SpendJson spend) {
        selectSpendingCategoryDropDown.click();
        selectSpendingCategoryDropDown.$$("#react-select-3-listbox").find(text("рыбалка")).click();
        setAmountInput.setValue(String.valueOf(spend.getAmount()));
        spendingDescriptionInput.setValue(spend.getDescription());
        return this;
    }

    @Step("Check spend in history area")
    public MainPage checkSpendsInHistory(SpendJson spendJson) {
        $(".spendings__content tbody")
                .$$("tr")
                .find(text(spendJson.getDescription()))
                .$$("td")
                .first()
                .scrollTo()
                .click();
        return this;
    }

    @Step("Delete spend in history area")
    public MainPage deleteSpendInHistory(SpendJson spendJson) {
        $(".spendings__content tbody")
                .$$("tr")
                .find(text(spendJson.getDescription()))
                .$$("td")
                .first()
                .scrollTo()
                .click();

        $(byText("Delete selected")).click();

        $(".spendings__content tbody")
                .$$("tr")
                .find(text(spendJson.getDescription()))
                .$$("td")
                .shouldHave(size(0));
        return this;
    }


}
