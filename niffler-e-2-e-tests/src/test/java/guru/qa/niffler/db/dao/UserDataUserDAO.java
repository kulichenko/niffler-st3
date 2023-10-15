package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.userdata.UserDataUserEntity;

public interface UserDataUserDAO {
    int createUserInUserData(UserDataUserEntity user);

    void deleteUserByUsernameInUserData(String username);

    void deleteUserFromUserData(UserDataUserEntity user);

    UserDataUserEntity getUserInUserDataByUsername(String username);

    void addFriendForUser(boolean pending, UserDataUserEntity user, UserDataUserEntity friend);
}
