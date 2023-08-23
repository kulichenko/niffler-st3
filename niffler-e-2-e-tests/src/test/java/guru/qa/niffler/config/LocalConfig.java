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
}
