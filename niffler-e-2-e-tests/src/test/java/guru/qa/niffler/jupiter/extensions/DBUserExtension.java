package guru.qa.niffler.jupiter.extensions;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.userdata.UserDataUserEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Arrays;

public class DBUserExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DBUserExtension.class);
    private static final String AUTH_USER_DAO = "authUserDAO";
    private static final String USER_DATA_USER_DAO = "userDataUserDAO";
    private static final String AUTH_USER = "authUser";
    private static final String USER_DATA_USER = "userDataUser";

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        DBUser annotation = context.getRequiredTestMethod().getAnnotation(DBUser.class);

        if (annotation != null) {
            AuthUserEntity authUserEntity = new AuthUserEntity();
            UserDataUserEntity userDataUserEntity = new UserDataUserEntity();
            Faker faker = new Faker();
            if (annotation.username().isEmpty()) {
                authUserEntity.setUsername(faker.name().username());
            } else {
                authUserEntity.setUsername(annotation.username());
            }
            if (annotation.password().isEmpty()) {
                authUserEntity.setPassword(faker.internet().password());
            } else {
                authUserEntity.setPassword(annotation.password());
            }
            authUserEntity.setEnabled(true);
            authUserEntity.setAccountNonExpired(true);
            authUserEntity.setAccountNonLocked(true);
            authUserEntity.setCredentialsNonExpired(true);
            authUserEntity.setAuthorities(Arrays.stream(Authority.values())
                    .map(authority -> {
                        var ae = new AuthorityEntity();
                        ae.setAuthority(authority);
                        ae.setUser(authUserEntity);
                        return ae;
                    }).toList()
            );
            AuthUserDAO authUserDAO = (AuthUserDAO) context.getStore(NAMESPACE).get(AUTH_USER_DAO);
            UserDataUserDAO userDataUserDAO = (UserDataUserDAO) context.getStore(NAMESPACE).get(USER_DATA_USER_DAO);
            var userId = authUserDAO.createUser(authUserEntity);
//            authUserEntity.setId(userId);
            userDataUserEntity.setUsername(authUserEntity.getUsername());
            userDataUserEntity.setCurrency(CurrencyValues.RUB);
            userDataUserDAO.createUserInUserData(userDataUserEntity);
            context.getStore(NAMESPACE).put(AUTH_USER, authUserEntity);
            context.getStore(NAMESPACE).put(USER_DATA_USER, userDataUserEntity);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        var authUserEntity = context.getStore(NAMESPACE).get(AUTH_USER, AuthUserEntity.class);
        var userUserData = context.getStore(NAMESPACE).get(USER_DATA_USER, UserDataUserEntity.class);
        AuthUserDAO authUserDAO = (AuthUserDAO) context.getStore(NAMESPACE).get(AUTH_USER_DAO);
        UserDataUserDAO userDataUserDAO = (UserDataUserDAO) context.getStore(NAMESPACE).get(USER_DATA_USER_DAO);
        userDataUserDAO.deleteUserFromUserData(userUserData);
        authUserDAO.deleteUserById(authUserEntity);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext
                .getParameter()
                .getType()
                .isAssignableFrom(AuthUserEntity.class);
    }

    @Override
    public AuthUserEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(DBUserExtension.NAMESPACE).get(AUTH_USER, AuthUserEntity.class);
    }
}