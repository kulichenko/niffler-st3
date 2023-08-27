package guru.qa.niffler.jupiter;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserEntity;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Arrays;

public class DBUserExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DBUserExtension.class);
    private AuthUserDAO authUserDAO;
    private UserDataUserDAO userDataUserDAO;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        DBUser annotation = context.getRequiredTestMethod().getAnnotation(DBUser.class);

        if (annotation != null) {
            UserEntity user = new UserEntity();
            Faker faker = new Faker();
            if (annotation.username().isEmpty()) {
                user.setUsername(faker.name().username());
            } else {
                user.setUsername(annotation.username());
            }
            if (annotation.password().isEmpty()) {
                user.setPassword(faker.internet().password());
            } else {
                user.setPassword(annotation.password());
            }
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            user.setAuthorities(Arrays.stream(Authority.values())
                    .map(authority -> {
                        var ae = new AuthorityEntity();
                        ae.setAuthority(authority);
                        return ae;
                    }).toList()
            );
            authUserDAO = (AuthUserDAO) context.getStore(NAMESPACE).get("authUserDAO");
            userDataUserDAO = (UserDataUserDAO) context.getStore(NAMESPACE).get("userDataUserDAO");
            var userId = authUserDAO.createUser(user);
            user.setId(userId);
            userDataUserDAO.createUserInUserData(user);
            context.getStore(NAMESPACE).put("user", user);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        var user = context.getStore(NAMESPACE).get("user", UserEntity.class);
        authUserDAO = (AuthUserDAO) context.getStore(NAMESPACE).get("authUserDAO");
        userDataUserDAO = (UserDataUserDAO) context.getStore(NAMESPACE).get("userDataUserDAO");
        userDataUserDAO.deleteUserByUsernameInUserData(user.getUsername());
        authUserDAO.deleteUserById(user.getId());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext
                .getParameter()
                .getType()
                .isAssignableFrom(UserEntity.class);
    }

    @Override
    public UserEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(DBUserExtension.NAMESPACE).get("user", UserEntity.class);
    }
}
