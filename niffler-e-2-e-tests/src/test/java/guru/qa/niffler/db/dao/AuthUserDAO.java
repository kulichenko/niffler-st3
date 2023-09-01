package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

//Паттерн DAO - Отвязать реализацию хранения данных от того как программа взаимодействует с этими данными.
public interface AuthUserDAO {
    PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    UUID createUser(AuthUserEntity user);

    void deleteUserById(AuthUserEntity user);

    AuthUserEntity getUserById(UUID userId);

    AuthUserEntity updateUser(AuthUserEntity user);
}
