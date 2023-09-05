package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import guru.qa.niffler.jupiter.annotations.WebTest;

@WebTest
public abstract class BaseWebTest {

    static {
        Configuration.browser = "firefox";
        Configuration.browserBinary = "C:\\Program Files\\Firefox 102.7\\firefox.exe";
        Configuration.browserSize = "1980x1024";

    }
}
