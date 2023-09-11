package guru.qa.niffler.jupiter.extensions;

import com.github.javafaker.Faker;
import guru.qa.niffler.api.SpendService;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.spend.CategoryEntity;
import guru.qa.niffler.jupiter.annotations.AddCategoryToDB;
import guru.qa.niffler.jupiter.annotations.AddSpendViaAPI;
import guru.qa.niffler.jupiter.annotations.AddUserToDB;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Date;

import static guru.qa.niffler.jupiter.extensions.DBUserExtension.AUTH_USER;

public class SpendExtension implements BeforeEachCallback, ParameterResolver {

    public static ExtensionContext.Namespace SPEND_NAMESPACE = ExtensionContext.Namespace.create(SpendExtension.class);
    public final String SPEND = "spend";

    private static final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private static final Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient)
            .baseUrl("http://127.0.0.1:8093")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private SpendService spendService = retrofit.create(SpendService.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AddSpendViaAPI annotation = context.getRequiredTestMethod().getAnnotation(AddSpendViaAPI.class);
        if (annotation != null) {
            SpendJson spend = new SpendJson();
            Faker faker = new Faker();
            if (!annotation.username().isEmpty()) {
                spend.setUsername(annotation.username());
            } else {
                AddUserToDB userToDB = context.getRequiredTestMethod().getAnnotation(AddUserToDB.class);
                if (userToDB != null) {
                    AuthUserEntity authUserEntity = context.getStore(DBUserExtension.NAMESPACE_USER).get(AUTH_USER, AuthUserEntity.class);
                    spend.setUsername(authUserEntity.getUsername());
                }
            }
            if (annotation.description().isEmpty()) {
                spend.setDescription(faker.commerce().productName());
            } else {
                spend.setDescription(annotation.description());
            }

            if (annotation.amount() == -1.0) {
                spend.setAmount(Double.valueOf(faker.commerce().price().replace(",", ".")));
            } else {
                spend.setAmount(annotation.amount());
            }

            if (annotation.category().isEmpty()) {
                AddCategoryToDB category = context.getRequiredTestMethod().getAnnotation(AddCategoryToDB.class);
                if (category != null) {
                    CategoryEntity categoryEntity = context.getStore(DBCategoryExtension.NAMESPACE_CATEGORY).get(DBCategoryExtension.CATEGORY_ENTITY, CategoryEntity.class);
                    spend.setCategory(categoryEntity.getCategory());
                }
            } else {
                spend.setCategory(annotation.category());
            }
            spend.setSpendDate(new Date());
            spend.setCurrency(annotation.currency());
            SpendJson createdSpend = spendService.addSpend(spend).execute().body();
            context.getStore(SPEND_NAMESPACE).put(SPEND, createdSpend);
        }
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext
                .getStore(SpendExtension.SPEND_NAMESPACE)
                .get(SPEND, SpendJson.class);
    }
}
