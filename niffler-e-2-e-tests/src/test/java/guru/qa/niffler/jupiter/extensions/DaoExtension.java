package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.impl.AuthUserDAOHibernate;
import guru.qa.niffler.db.dao.impl.AuthUserDAOSpringJdbc;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.dao.impl.UserDataUserDAOHibernate;
import guru.qa.niffler.jupiter.annotations.Dao;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

import static guru.qa.niffler.jupiter.extensions.DBUserExtension.NAMESPACE_USER;

public class DaoExtension implements TestInstancePostProcessor {
    private static final String DB_IMPL = System.getProperty("db.impl");
    private static final String AUTH_USER_DAO = "authUserDAO";
    private static final String USER_DATA_USER_DAO = "userDataUserDAO";

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(AuthUserDAO.class) || field.getType().isAssignableFrom(UserDataUserDAO.class)
                    && field.isAnnotationPresent(Dao.class)) {
                field.setAccessible(true);

                AuthUserDAO dao;
                UserDataUserDAO userDataUserDAO = null;

                if ("hibernate".equals(DB_IMPL)) {
                    dao = new AuthUserDAOHibernate();
                } else if ("spring".equals(DB_IMPL)) {
                    dao = new AuthUserDAOSpringJdbc();
                } else {
                    dao = new AuthUserDAOHibernate();
                    userDataUserDAO = new UserDataUserDAOHibernate();
/*
                    dao = new AuthUserDAOSpringJdbc();
                    dao = new AuthUserDAOJdbc();
*/
                }
                if (field.getType().isAssignableFrom(AuthUserDAO.class)) {
                    context.getStore(NAMESPACE_USER).put(AUTH_USER_DAO, dao);
                    field.set(testInstance, dao);
                } else if (field.getType().isAssignableFrom(UserDataUserDAO.class)) {
                    context.getStore(NAMESPACE_USER).put(USER_DATA_USER_DAO, userDataUserDAO);
                    field.set(testInstance, userDataUserDAO);
                    System.out.println();
                }
            }
        }
    }
}
