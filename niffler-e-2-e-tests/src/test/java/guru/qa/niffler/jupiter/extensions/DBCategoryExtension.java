package guru.qa.niffler.jupiter.extensions;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.dao.SpendDao;
import guru.qa.niffler.db.dao.impl.SpendDAOHibernate;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.spend.CategoryEntity;
import guru.qa.niffler.jupiter.annotations.AddCategoryToDB;
import guru.qa.niffler.jupiter.annotations.AddUserToDB;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class DBCategoryExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    protected static final String CATEGORY_ENTITY = "categoryEntity";
    private static final String AUTH_USER = "authUser";
    public static ExtensionContext.Namespace NAMESPACE_CATEGORY = ExtensionContext.Namespace.create(DBCategoryExtension.class);
    private final SpendDao spendDao = new SpendDAOHibernate();

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        AddCategoryToDB annotation = extensionContext.getRequiredTestMethod().getAnnotation(AddCategoryToDB.class);
        if (annotation != null) {
            CategoryEntity categoryEntity = new CategoryEntity();
            Faker faker = new Faker();
            if (!annotation.category().isEmpty()) {
                categoryEntity.setCategory(annotation.category());
            } else categoryEntity.setCategory(faker.commerce().material());
            if (!annotation.username().isEmpty()) {
                categoryEntity.setUsername(annotation.username());
            } else {
                AddUserToDB userToDB = extensionContext.getRequiredTestMethod().getAnnotation(AddUserToDB.class);
                if (userToDB != null) {
                    AuthUserEntity authUserEntity = extensionContext.getStore(DBUserExtension.NAMESPACE_USER).get(AUTH_USER, AuthUserEntity.class);
                    categoryEntity.setUsername(authUserEntity.getUsername());
                }
            }
            spendDao.createCategory(categoryEntity);
            extensionContext.getStore(NAMESPACE_CATEGORY).put(CATEGORY_ENTITY, categoryEntity);
        }

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryEntity.class);
    }

    @Override
    public CategoryEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(DBCategoryExtension.NAMESPACE_CATEGORY).get(CATEGORY_ENTITY, CategoryEntity.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        var categoryEntity = extensionContext.getStore(NAMESPACE_CATEGORY).get(CATEGORY_ENTITY, CategoryEntity.class);
        var spendEntity = spendDao.getSpendByCategory(categoryEntity);
        if (spendEntity != null) {
            spendDao.deleteSpend(spendEntity);
        }
        spendDao.deleteCategory(categoryEntity);

    }
}
