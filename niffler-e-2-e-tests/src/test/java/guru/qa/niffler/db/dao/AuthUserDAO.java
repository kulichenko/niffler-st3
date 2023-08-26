package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

//Паттерн DAO - Отвязать реализацию хранения данных от того как программа взаимодействует с этими данными.
public interface AuthUserDAO {
    PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    int createUser(UserEntity user);

    void deleteUserById(UUID userId);

    UserEntity getUserById(UUID userId);

    int updateUser(UserEntity user);
}
