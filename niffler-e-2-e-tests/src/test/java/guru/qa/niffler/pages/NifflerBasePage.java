package guru.qa.niffler.pages;

import guru.qa.niffler.config.Config;

public abstract class NifflerBasePage {
    protected static final Config cfg = Config.getInstance();

    public abstract <T extends NifflerBasePage> T checkPageContent();

    public abstract <T extends NifflerBasePage> T checkPageLoaded();

}
