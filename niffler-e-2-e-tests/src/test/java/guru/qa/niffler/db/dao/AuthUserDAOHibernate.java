package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserEntity;

import java.util.UUID;

public class AuthUserDAOHibernate implements AuthUserDAO {
    @Override
    public UUID createUser(UserEntity user) {
        return user.getId();
    }

    @Override
    public void deleteUserById(UUID userId) {

    }

    @Override
    public UserEntity getUserById(UUID userId) {
        return null;
    }

    @Override
    public int updateUser(UserEntity user) {
        return 0;
    }
}
