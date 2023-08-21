package guru.qa.niffler.jupiter;

import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static guru.qa.niffler.jupiter.User.UserType.INVITATION_RECEIVED;
import static guru.qa.niffler.jupiter.User.UserType.INVITATION_SENT;
import static guru.qa.niffler.jupiter.User.UserType.WITH_FRIENDS;

public class UserQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {
    private static final Map<User.UserType, Queue<UserJson>> usersQueue = new ConcurrentHashMap<>();
    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserQueueExtension.class);

    static {
        Queue<UserJson> usersWithFriends = new ConcurrentLinkedQueue<>();
        usersWithFriends.add(bindUser("dasha", "12345"));
        usersWithFriends.add(bindUser("sasha", "12345"));
        usersWithFriends.add(bindUser("maksim", "12345"));
        usersQueue.put(WITH_FRIENDS, usersWithFriends);
        Queue<UserJson> usersInvSent = new ConcurrentLinkedQueue<>();
        usersInvSent.add(bindUser("vanya", "12345"));
        usersInvSent.add(bindUser("sasha", "12345"));
        usersInvSent.add(bindUser("maksim", "12345"));
        usersQueue.put(INVITATION_SENT, usersInvSent);
        Queue<UserJson> usersInvReceived = new ConcurrentLinkedQueue<>();
        usersInvReceived.add(bindUser("ira", "12345"));
        usersInvReceived.add(bindUser("sveta", "12345"));
        usersInvReceived.add(bindUser("danya", "12345"));
        usersQueue.put(INVITATION_RECEIVED, usersInvReceived);
    }

    private static UserJson bindUser(String username, String password) {
        var user = new UserJson();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        var beforeEachMethod =
                Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(BeforeEach.class)).findFirst();
        if (beforeEachMethod.isPresent()) {
            Parameter[] parameters = beforeEachMethod.get().getParameters();
            for (Parameter parameter : parameters) {
                if (parameter.getType().isAssignableFrom(UserJson.class)) {
                    User parameterAnnotation = parameter.getAnnotation(User.class);
                    User.UserType userType = parameterAnnotation.userType();
                    Queue<UserJson> usersQueueByType = usersQueue.get(userType);
                    UserJson candidateForTest = null;
                    while (candidateForTest == null) {
                        candidateForTest = usersQueueByType.poll();
                    }
                    candidateForTest.setUserType(userType);
                    context.getStore(NAMESPACE).put(getAllureId(context), candidateForTest);
                    break;
                }
            }
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        UserJson userFromTest = context.getStore(NAMESPACE).get(getAllureId(context), UserJson.class);
        usersQueue.get(userFromTest.getUserType()).add(userFromTest);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class)
                && parameterContext.getParameter().isAnnotationPresent(User.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(getAllureId(extensionContext), UserJson.class);
    }

    private String getAllureId(ExtensionContext context) {
        var allureId = context.getRequiredTestMethod().getAnnotation(AllureId.class);
        if (allureId == null) {
            throw new IllegalStateException("Annotation @AllureId must be present!");
        }
        return allureId.value();
    }
}
