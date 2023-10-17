package guru.qa.niffler.pages;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Selenide.$;

public class FriendsPage extends NifflerBasePage {
    public static final String URL = "/friends";

    public Header header = new Header();

    @Override
    public FriendsPage checkPageContent() {
        $(".people-content").should(Condition.visible);
        return this;
    }


    @Override
    public <T extends NifflerBasePage> T checkPageLoaded() {
        return null;
    }
}
