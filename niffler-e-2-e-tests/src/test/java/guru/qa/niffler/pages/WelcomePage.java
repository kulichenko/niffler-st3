package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class WelcomePage extends NifflerBasePage {
    public static final String URL = "/";

    private final SelenideElement
            welcomeHeader = $("h1"),
            loginButton = $("a[href*='redirect']"),
            registerButton = $("a[href*='http://127.0.0.1:9000/register']");

    @Step("Проверка контента страницы WelcomePage")
    @SuppressWarnings("unchecked")
    @Override
    public WelcomePage checkPageContent() {
        welcomeHeader.should(visible);
        loginButton.should(visible);
        registerButton.should(visible);
        return this;
    }

    @Step("Проверка, что страница WelcomePage загрузилась")
    @SuppressWarnings("unchecked")
    @Override
    public WelcomePage checkPageLoaded() {
        welcomeHeader.should(visible);
        return this;
    }

    public LoginPage goToLoginPage() {
        loginButton.click();
        return new LoginPage();
    }

    public RegisterPage signUp() {

        return new RegisterPage();
    }

}
