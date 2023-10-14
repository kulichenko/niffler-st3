package guru.qa.niffler.test.web;

import com.codeborne.selenide.Configuration;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotations.WebTest;

@WebTest
public abstract class BaseWebTest {

    protected static final Config cfg = Config.getInstance();

    static {
        Configuration.browser = "firefox";
        Configuration.browserBinary = "C:\\Program Files\\Firefox 102.7\\firefox.exe";
        Configuration.browserSize = "1980x1024";

    }
}
