package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.userdata.UserDataUserEntity;

public interface UserRepository {
    void createUserForTest(AuthUserEntity user);

    void addFriendForUser(boolean pending, UserDataUserEntity user, UserDataUserEntity friend);

    void removeAfterTest(AuthUserEntity user);

    UserDataUserEntity getUserInUserDataByUsername(String username);
}