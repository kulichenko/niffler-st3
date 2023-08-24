package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserEntity;

import java.util.UUID;

public class AuthUserDAOSpringJdbc implements AuthUserDAO {
    @Override
    public int createUser(UserEntity user) {
        return 0;
    }

    @Override
    public void deleteUserById(UUID userId) {

    }

    @Override
    public UserEntity getUserFromDBById(UUID userId) {
        return null;
    }

    @Override
    public int updateUser(UserEntity user) {
        return 0;
    }
}
