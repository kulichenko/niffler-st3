package guru.qa.niffler.api;

import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserDataService {

    @GET("/currentUser")
    Call<UserJson> currentUser(@Header("Authorization") String token,
                               @Header("Cookie") String jsessionid);

    @POST("/addFriend")
    Call<Void> addFriend(@Header("Authorization") String token,
                         @Header("Cookie") String jsessionid,
                         @Body UserJson user);

    @POST("/acceptInvitation")
    Call<Void> acceptInvitation(@Header("Authorization") String token,
                                @Header("Cookie") String jsessionid,
                                @Body UserJson user);
}
