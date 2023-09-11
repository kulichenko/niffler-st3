package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class RegisterPage extends NifflerBasePage {
    private SelenideElement
            usernameInput = $("#username"),
            passwordInput = $("#password"),
            passwordSubmitInput = $("#passwordSubmit"),
            submitButton = $(".form__submit");

    @Override
    public <T extends NifflerBasePage> T checkPageContent() {
        return null;
    }

    @Override
    public RegisterPage checkPageLoaded() {
        $("#register-form").should(visible);
        usernameInput.should(visible);
        passwordInput.should(visible);
        passwordSubmitInput.should(visible);
        submitButton.should(visible);
        return this;
    }

    public LoginPage registerNewUser(String user, String pass) {
        usernameInput.setValue(user);
        passwordInput.setValue(pass);
        passwordSubmitInput.setValue(pass);
        submitButton.click();
        $(".form__paragraph").shouldHave(text("Congratulations! You've registered!"));
        $$(".form__paragraph").find(text("Sign in!")).click();
        return new LoginPage();
    }

    public RegisterPage registerExistUser(String user, String pass) {
        usernameInput.setValue(user);
        passwordInput.setValue(pass);
        passwordSubmitInput.setValue(pass);
        submitButton.click();
        return new RegisterPage();
    }

    public RegisterPage checkExistUser(String username) {
        $(".form__error").shouldHave(text("Username `" + username + "` already exists"));
        return this;
    }

}
