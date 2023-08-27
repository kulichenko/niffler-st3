package guru.qa.niffler.config;

public class DockerConfig implements Config {

    //package access для того, чтобы исключить возможность создания конфига из других покетов
    static final DockerConfig config = new DockerConfig();

    private DockerConfig() {
    }

    @Override
    public String databaseHost() {
        return "niffler-all-db";
    }
}
