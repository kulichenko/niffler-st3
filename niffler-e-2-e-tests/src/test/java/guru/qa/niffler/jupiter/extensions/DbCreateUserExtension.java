package guru.qa.niffler.jupiter.extensions;


import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.model.userdata.UserDataUserEntity;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryHibernate;
import guru.qa.niffler.db.repository.UserRepositorySpringJdbc;
import guru.qa.niffler.jupiter.annotations.GenerateUser;
import guru.qa.niffler.model.UserJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static guru.qa.niffler.util.FakerUtils.generateRandomName;

public class DbCreateUserExtension extends CreateUserExtension {

    private static final String DEFAULT_PASSWORD = "12345";
    private final UserRepository userRepository = new UserRepositorySpringJdbc();
    private final UserRepository userRepositoryHibernate = new UserRepositoryHibernate();

    @Override
    protected UserJson createUserForTest(GenerateUser annotation) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(generateRandomName());
        authUser.setPassword(DEFAULT_PASSWORD);
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(new ArrayList<>(Arrays.stream(Authority.values())
                .map(a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    ae.setUser(authUser);
                    return ae;
                }).toList()));

        userRepositoryHibernate.createUserForTest(authUser);
        UserJson result = UserJson.fromEntity(authUser);
        result.setPassword(DEFAULT_PASSWORD);
        return result;
    }

    @Override
    protected List<UserJson> createFriendsIfPresent(GenerateUser annotation, UserJson currentUser) {
        List<UserJson> result = new ArrayList<>();
        UserDataUserEntity currentUserEntity = userRepositoryHibernate.getUserInUserDataByUsername(currentUser.getUsername());

        if (annotation.friends().handleAnnotation()) {
            int friendsQty = annotation.friends().count();
            for (int i = 0; i < friendsQty; i++) {
                UserJson friend = createUserForTest(annotation);
                friend.setFriends(Collections.singletonList(currentUser));
                result.add(friend);
                userRepositoryHibernate.addFriendForUser(false, currentUserEntity, userRepositoryHibernate.getUserInUserDataByUsername(friend.getUsername()));
                userRepositoryHibernate.addFriendForUser(false, userRepositoryHibernate.getUserInUserDataByUsername(friend.getUsername()), currentUserEntity);
            }
        }
        currentUser.setFriends(result);

        return result;
    }

    @Override
    protected List<UserJson> createIncomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) {
        List<UserJson> result = new ArrayList<>();
        UserDataUserEntity currentUserEntity = userRepositoryHibernate.getUserInUserDataByUsername(currentUser.getUsername());

        if (annotation.incomeInvitations().handleAnnotation()) {
            int invitationsQty = annotation.incomeInvitations().count();
            for (int i = 0; i < invitationsQty; i++) {
                UserJson friend = createUserForTest(annotation);
                friend.setOutcomeInvitations(Collections.singletonList(currentUser));
                result.add(friend);
                userRepositoryHibernate.addFriendForUser(true, userRepositoryHibernate.getUserInUserDataByUsername(friend.getUsername()), currentUserEntity);
            }
        }
        currentUser.setIncomeInvitations(result);
        return result;
    }

    @Override
    protected List<UserJson> createOutcomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) {
        List<UserJson> result = new ArrayList<>();
        UserDataUserEntity currentUserEntity = userRepositoryHibernate.getUserInUserDataByUsername(currentUser.getUsername());

        if (annotation.outcomeInvitations().handleAnnotation()) {
            int invitationsQty = annotation.outcomeInvitations().count();
            for (int i = 0; i < invitationsQty; i++) {
                UserJson friend = createUserForTest(annotation);
                friend.setIncomeInvitations(Collections.singletonList(currentUser));
                result.add(friend);
                userRepositoryHibernate.addFriendForUser(true, currentUserEntity, userRepositoryHibernate.getUserInUserDataByUsername(friend.getUsername()));
            }
        }
        currentUser.setOutcomeInvitations(result);
        return result;
    }
}