package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.api.AuthServiceClient;
import guru.qa.niffler.api.UserDataService;
import guru.qa.niffler.api.UserService;
import guru.qa.niffler.api.context.CookieContext;
import guru.qa.niffler.api.context.SessionStorageContext;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotations.GenerateUser;
import guru.qa.niffler.model.UserJson;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static guru.qa.niffler.util.FakerUtils.generateRandomName;

public class RestCreateUserExtension extends CreateUserExtension {
    protected static final Config CFG = Config.getInstance();
    private static final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private static final Retrofit userRetrofit = new Retrofit.Builder()
            .client(httpClient)
            .baseUrl(CFG.nifflerAuthUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private static final UserService userService = userRetrofit.create(UserService.class);
    private static final Retrofit userDataRetrofit = new Retrofit.Builder()
            .client(httpClient)
            .baseUrl(CFG.nifflerUserDataUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private static final UserDataService userDataService = userDataRetrofit.create(UserDataService.class);
    private static final String DEFAULT_PASSWORD = "12345";
    private final AuthServiceClient authService = new AuthServiceClient();

    @Override
    protected UserJson createUserForTest(GenerateUser annotation) throws IOException {
        var authUser = new UserJson();
        authUser.setUsername(generateRandomName());
        System.out.println(authUser.getUsername());
        authUser.setPassword(DEFAULT_PASSWORD);
        var xsrfToken = getCsrf();
        userService.register
                (
                        "XSRF-TOKEN=" + xsrfToken,
                        xsrfToken,
                        authUser.getUsername(),
                        authUser.getPassword(),
                        authUser.getPassword()
                ).execute();

        return authUser;
    }

    @Override
    protected List<UserJson> createFriendsIfPresent(GenerateUser annotation, UserJson currentUser) throws IOException {
        List<UserJson> result = new ArrayList<>();
        authService.doLogin(currentUser.getUsername(), currentUser.getPassword());
        if (annotation.friends().handleAnnotation()) {
            int friendsQty = annotation.friends().count();
            for (int i = 0; i < friendsQty; i++) {
                UserJson friend = createUserForTest(annotation);
                friend.setFriends(Collections.singletonList(currentUser));
                result.add(friend);
                sendInvitation(currentUser, friend);
                acceptInvitation(currentUser, friend);
            }
        }
        currentUser.setFriends(result);
        return result;
    }

    @Override
    protected List<UserJson> createIncomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) throws IOException {
        List<UserJson> result = new ArrayList<>();
        if (annotation.incomeInvitations().handleAnnotation()) {
            int invitationsQty = annotation.incomeInvitations().count();
            for (int i = 0; i < invitationsQty; i++) {
                UserJson friend = createUserForTest(annotation);
                friend.setOutcomeInvitations(Collections.singletonList(currentUser));
                result.add(friend);
                sendInvitation(friend, currentUser);
            }
        }
        currentUser.setIncomeInvitations(result);
        return result;
    }

    @Override
    protected List<UserJson> createOutcomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) throws IOException {
        List<UserJson> result = new ArrayList<>();
        if (annotation.outcomeInvitations().handleAnnotation()) {
            int invitationsQty = annotation.outcomeInvitations().count();
            for (int i = 0; i < invitationsQty; i++) {
                UserJson friend = createUserForTest(annotation);
                friend.setOutcomeInvitations(Collections.singletonList(currentUser));
                result.add(friend);
                sendInvitation(currentUser, friend);
            }
        }
        currentUser.setIncomeInvitations(result);
        return result;
    }

    private void sendInvitation(UserJson currentUser, UserJson friend) throws IOException {
        SessionStorageContext sessionStorageContext = SessionStorageContext.getInstance();
        CookieContext cookieContext = CookieContext.getInstance();
        authService.doLogin(currentUser.getUsername(), currentUser.getPassword());
        var token = "Bearer " + sessionStorageContext.getToken();
        var jSession = "JSESSIONID=" + cookieContext.getJSessionIdCookieValue();
        userDataService.addFriend(
                        token,
                        jSession,
                        friend)
                .execute();
        sessionStorageContext.clearContext();
        cookieContext.clearContext();
    }

    private void acceptInvitation(UserJson currentUser, UserJson friend) throws IOException {
        SessionStorageContext sessionStorageContext = SessionStorageContext.getInstance();
        CookieContext cookieContext = CookieContext.getInstance();
        authService.doLogin(friend.getUsername(), friend.getPassword());
        var token = "Bearer " + sessionStorageContext.getToken();
        var jSession = "JSESSIONID=" + cookieContext.getJSessionIdCookieValue();
        userDataService.acceptInvitation(
                        token,
                        jSession,
                        currentUser)
                .execute();
        sessionStorageContext.clearContext();
        cookieContext.clearContext();
    }

    private String getCsrf() throws IOException {
        var execute = userService.register().execute();
        return execute.headers().get("X-XSRF-TOKEN");
    }
}
