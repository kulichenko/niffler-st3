package guru.qa.niffler.pages;

import static com.codeborne.selenide.Selenide.$;

public class Header {
    public MainPage goToMainPage() {
        $("li[data-tooltip-content='main']").click();
        return new MainPage();
    }

    public FriendsPage goToFriendPage() {
        $("li[data-tooltip-content='friends']").click();
        return new FriendsPage();
    }

    public PeoplePage goToPeoplePage() {
        $("li[data-tooltip-content='All people']").click();
        return new PeoplePage();
    }

    public ProfilePage goToProfilePage() {
        $("li[data-tooltip-content='Profile']").click();
        return new ProfilePage();
    }
}
