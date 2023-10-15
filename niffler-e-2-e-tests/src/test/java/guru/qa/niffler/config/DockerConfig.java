package guru.qa.niffler.config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;

public class DockerConfig implements Config {

    //package access для того, чтобы исключить возможность создания конфига из других покетов
    static final DockerConfig config = new DockerConfig();

    private DockerConfig() {
    }

    static {
        Selenide.open();
        Configuration.remote = "http://selenide:4444";
    }

    @Override
    public String databaseHost() {
        return "niffler-all-db";
    }

    @Override
    public String baseUrl() {
        return null;
    }

    @Override
    public String getCurrencyGrpcAddress() {
        return "niffler-currency";
    }

    @Override
    public int getCurrencyGrpcPort() {
        return 8092;
    }

    @Override
    public String nifflerAuthUrl() {
        return "http://auth.niffler.dc:9000";
    }

    @Override
    public String nifflerSpendUrl() {
        return "niffler-spend:8093";
    }
}
