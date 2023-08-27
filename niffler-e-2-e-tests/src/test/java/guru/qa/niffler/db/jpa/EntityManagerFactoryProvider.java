package guru.qa.niffler.db.jpa;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.ServiceDB;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EntityManagerFactoryProvider {
    INSTANCE;
    private static final Config cfg = Config.getInstance();
    private final Map<ServiceDB, EntityManagerFactory> dataSourceStore = new ConcurrentHashMap<>();

    public EntityManagerFactory getDataSource(ServiceDB db) {
        return dataSourceStore.computeIfAbsent(db, key -> {
            Map<String, Object> props = new HashMap<>();
            props.put("hibernate.connection.url", db.getUrl());
            props.put("hibernate.connection.user", cfg.dataBaseUser());
            props.put("hibernate.connection.password", cfg.dataBasePassword());
            props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

            /**
             * Применен паттерн проектирования "декоратор". Используется для того чтобы
             * декоратор позволяет нам динамически добавлять функциональность объекту без
             * влияния на поведение объектов того же самого класса.
             * Создаются классы-декораторы, которые оборачивают исходный класс и предоставляют
             * дополнительную функциональность, сохраняя сигнатуры методов исходного класса нетронутыми
             */

            EntityManagerFactory entityManagerFactory =
                    new ThreadLocalEntityManagerFactory(
                            Persistence.createEntityManagerFactory("niffler-st3", props)
                    );
            return entityManagerFactory;
        });
    }

}
