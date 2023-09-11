package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends NifflerBasePage {
    private final SelenideElement
            usernameInput = $("input[name='username']"),
            passwordInput = $("input[name='password']"),
            submitButton = $(".form__submit");

    @Override
    public <T extends NifflerBasePage> T checkPageContent() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Step("Проверка, что страница LoginPage загрузилась")
    public LoginPage checkPageLoaded() {
        usernameInput.should(visible);
        passwordInput.should(visible);
        submitButton.should(interactable);
        return this;
    }

    @Step("Логин пользователя {}")
    public MainPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage();
    }

    @Step("Try login with wrong username")
    public LoginPage wrongUsernameLogin(String username, String password) {
        usernameInput.setValue(username + "something");
        passwordInput.setValue(password);
        submitButton.click();
        $(".form__error").shouldHave(text("Неверные учетные данные пользователя"));
        return new LoginPage();
    }
}
