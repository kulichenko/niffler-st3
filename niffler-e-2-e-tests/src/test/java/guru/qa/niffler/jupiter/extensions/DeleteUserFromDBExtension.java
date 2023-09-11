package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.dao.impl.AuthUserDAOHibernate;
import guru.qa.niffler.db.dao.impl.UserDataUserDAOHibernate;
import guru.qa.niffler.jupiter.annotations.DeleteUserFromDB;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;


public class DeleteUserFromDBExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        DeleteUserFromDB annotation = extensionContext.getRequiredTestMethod().getAnnotation(DeleteUserFromDB.class);
        if (annotation != null) {
            AuthUserDAO dao = new AuthUserDAOHibernate();
            UserDataUserDAO userdataDAO = new UserDataUserDAOHibernate();
            userdataDAO.deleteUserByUsernameInUserData(annotation.user());
            dao.deleteUserByUsername(annotation.user());
        }

    }
}
