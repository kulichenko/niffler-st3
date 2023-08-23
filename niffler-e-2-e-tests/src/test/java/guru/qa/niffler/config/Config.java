package guru.qa.niffler.config;

public interface Config {

    static Config getInstance() {
        if ("docker".equals(System.getProperty("test.env"))) {
            return DockerConfig.config;
        }
        return LocalConfig.config;
    }

    String databaseHost();

    default String dataBaseUser() {
        return "postgres";
    }

    default String dataBasePassword() {
        return "secret";
    }

    default int dataBasePort() {
        return 5432;
    }


}
