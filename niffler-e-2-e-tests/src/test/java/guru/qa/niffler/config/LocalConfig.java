package guru.qa.niffler.config;

public class LocalConfig implements Config {

    //package access для того, чтобы исключить возможность создания конфига из других покетов
    static final LocalConfig config = new LocalConfig();

    private LocalConfig() {
    }

    @Override
    public String databaseHost() {
        return "localhost";
    }

    public String baseUrl() {
        return "http://127.0.0.1:3000";
    }

    @Override
    public String nifflerAuthUrl() {
        return "http://127.0.0.1:9000";
    }

    @Override
    public String nifflerSpendUrl() {
        return "http://127.0.0.1:8093";
    }
}
