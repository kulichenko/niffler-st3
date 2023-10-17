package guru.qa.niffler.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserService {

    @GET("/register")
    Call<Void> register();

    @FormUrlEncoded
    @POST(value = "/register")
    Call<Void> register(
            @Header("Cookie") String xsrf,
            @Field("_csrf") String _csrf,
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordSubmit
    );
}
