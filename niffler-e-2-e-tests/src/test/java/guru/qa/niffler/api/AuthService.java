package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {

    //init state вход на страницу http://127.0.0.1:3000/
    // 1. Пустые куки
    // 2. Session Storage:
    //      codeChallenge	2yuy0OS6XXsJv3CgxI2NHsQSW34KUOoe5iNh5PdjWIg
    //      codeVerifier	d2Ll5s4dI1OhMEGZpetovfq_qDjNDwYiB5bHdDHqMkY
    // Empty Local Storage


    @GET("/oauth2/authorize")
    Call<Void> authorize(
            @Query("response_type") String responseType,
            @Query("client_id") String clientId,
            @Query("scope") String scope,
            @Query(value = "redirect_uri", encoded = true) String redirectUri,
            @Query("code_challenge") String codeChallenge,
            @Query("code_challenge_method") String codeChallengeMethod
    );

    //first request
    //http://127.0.0.1:9000/oauth2/authorize?
    // response_type=code&
    // client_id=client&
    // scope=openid&
    // redirect_uri=http://127.0.0.1:3000/authorized&
    // code_challenge=2yuy0OS6XXsJv3CgxI2NHsQSW34KUOoe5iNh5PdjWIg&
    // code_challenge_method=S256

    //RESULT: 302 Set-Cookie:JSESSIONID=C7E375F1271D2E9B243C1D79614FB753;
    //go to location: (GET) http://127.0.0.1:9000/login (200) Set-Cookie: XSRF-TOKEN=44bdbca6-8998-459d-b4db-0d0632dde2dd
    //

    @POST("/login")
    @FormUrlEncoded
    Call<Void> login(
//            @Header("Cookie") String jsessionIdCookie,
//            @Header("Cookie") String xsrfTockenCookie,
            @Field("username") String username,
            @Field("password") String password,
            @Field("_csrf") String _csrf
    );

    //SECOND REQUEST
    // (POST) http://127.0.0.1:9000/login
    // payload: url encoded body
    // _csrf: 44bdbca6-8998-459d-b4db-0d0632dde2dd
    // username: maksim
    // password: 12345
    //REQ COOKIES:
    //Cookie:JSESSIONID=C7E375F1271D2E9B243C1D79614FB753; XSRF-TOKEN=44bdbca6-8998-459d-b4db-0d0632dde2dd
    //
    //RESULT: (302) Set-Cookie:JSESSIONID=CD8E0478114ACAB034AF821C72C2753C;Set-Cookie:XSRF-TOKEN=; очистка
    //Location:http://127.0.0.1:9000/oauth2/authorize?
    // response_type=code&
    // client_id=client&
    // scope=openid&
    // redirect_uri=http://127.0.0.1:3000/authorized&
    // code_challenge=2yuy0OS6XXsJv3CgxI2NHsQSW34KUOoe5iNh5PdjWIg&
    // code_challenge_method=S256&continue
    //
    // 2.1 REQUEST (redirect, GET)
    // Request URL:
    //http://127.0.0.1:9000/oauth2/authorize?response_type=code&client_id=client&scope=openid&redirect_uri=http://127.0.0.1:3000/authorized&code_challenge=2yuy0OS6XXsJv3CgxI2NHsQSW34KUOoe5iNh5PdjWIg&code_challenge_method=S256&continue
    //REQ COOKIES: Cookie:JSESSIONID=CD8E0478114ACAB034AF821C72C2753C
    //RESULT: 302
    //Location:http://127.0.0.1:3000/authorized?code=8XJArvS0YAV7WMCNwvRsO10jzobhFL9TKu79If7W9rFMh8i83nCR_zymEOyeRWWNQ_zQN__IP7IlfHP5EK9-s7gZ96OfhOhVM-oBdEP00tqIsbCUg_dAwLhkd9QTIy8p
    // 2.2 REQUEST (redirect, GET)
    //Request URL:
    //http://127.0.0.1:3000/authorized?code=8XJArvS0YAV7WMCNwvRsO10jzobhFL9TKu79If7W9rFMh8i83nCR_zymEOyeRWWNQ_zQN__IP7IlfHP5EK9-s7gZ96OfhOhVM-oBdEP00tqIsbCUg_dAwLhkd9QTIy8p
    //REQ COOKIES: Cookie:JSESSIONID=CD8E0478114ACAB034AF821C72C2753C
    //RESULT: 200

    @POST("/oauth2/token")
    Call<JsonNode> token(
            @Header("Authorization") String basicAuthorization,
            @Query("client_id") String clientId,
            @Query(value = "redirect_uri", encoded = true) String redirectUri,
            @Query("grant_type") String grantType,
            @Query("code") String code,
            @Query("code_verifier") String codeVerifier

    );

    //THIRD REQUEST
    //Request URL:
    //POST http://127.0.0.1:9000/oauth2/token?
    // client_id=client&
    // redirect_uri=http://127.0.0.1:3000/authorized&
    // grant_type=authorization_code&
    // code=8XJArvS0YAV7WMCNwvRsO10jzobhFL9TKu79If7W9rFMh8i83nCR_zymEOyeRWWNQ_zQN__IP7IlfHP5EK9-s7gZ96OfhOhVM-oBdEP00tqIsbCUg_dAwLhkd9QTIy8p&
    // code_verifier=d2Ll5s4dI1OhMEGZpetovfq_qDjNDwYiB5bHdDHqMkY
    //Authorization: Basic Y2xpZW50OnNlY3JldA==
    //RESULT:200
    //{
    //    "access_token": "eyJraWQiOiJhYmI0MDQ3MC05OTBhLTQ4NmUtYWQ0OS02MjA4ZjY1ZWU5ZTEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJtYWtzaW0iLCJhdWQiOiJjbGllbnQiLCJuYmYiOjE2OTU0ODM0NzQsInNjb3BlIjpbIm9wZW5pZCJdLCJpc3MiOiJodHRwOi8vMTI3LjAuMC4xOjkwMDAiLCJleHAiOjE2OTU1MTk0NzQsImlhdCI6MTY5NTQ4MzQ3NH0.X5RXBDE9GjHg_4-GGpCjZoImLUCwwXwtMyiylmssfje_MoHTUf9601XDWYK4Y0tIgA1Z4Im76OvHW5LlS0oOagu5it_PlaJcnKCcnAIPotKll9LIqQ_YSjEkB4rMUazAphZ6d3S2ogiBGwSKmOFPnFb4E_DJCRyvf-U89xJ9r4pRpIw5-J6-F0Z9MJqgFp59N8Wu0HQGmjYlgJ7t0eB5gZVTS_GMa8uTW8YZdKnZzYjatl4AINg0ELvA3LA2C7A_WochQ3HdZoKvZi1FD--cjdYpqVQoXMerWXqqP_EsFkiTLCFSbterkyQJR-BKqd2b2GisctTD36kTsR3RdTNgAA",
    //    "refresh_token": "lPIptR6NG_87GcmV9yk1sFVzmmP3yQ9WeU_517CSRW1BIVZWj7a_x5ltoACbrYize9SNNvyzytuXwFg_NqcjwvCzKYSyh21tPxk4I3bWkNayJ4SAv2bxsGe4vlE4PxFl",
    //    "scope": "openid",
    //    "id_token": "eyJraWQiOiJhYmI0MDQ3MC05OTBhLTQ4NmUtYWQ0OS02MjA4ZjY1ZWU5ZTEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJtYWtzaW0iLCJhdWQiOiJjbGllbnQiLCJhenAiOiJjbGllbnQiLCJhdXRoX3RpbWUiOjE2OTU0ODM0NzMsImlzcyI6Imh0dHA6Ly8xMjcuMC4wLjE6OTAwMCIsImV4cCI6MTY5NTQ4NTI3NCwiaWF0IjoxNjk1NDgzNDc0LCJzaWQiOiJEREdXRkVYNlVmVUtCUHU2NERvS0F3Nmt0QV9yLXlZQ1R3RUcwR0RNcDZJIn0.QD5R9oLFRhYyOQtp8kTIdhOcKm39FGC2rUWH5o9QFcPq2cv601KjtASsHzkhRssLsd8FhcPPjburuS9Ufil_0Jdvr1xC5ZZzDlrawSOZyvxx5HE7jj31mVB1j4fR2D3y-rXvWOSToTzSQ-dDXwPLSpxMiATe7O5Sfcki0k3QwUr-uNm-8_N5DTKlyCqatWi4b91vB74z9A1Mjxa-Pwb-1jEyEq1iT15rWxo9iHE9Sel6_DcUPCQmnEH4TeXhbrGXizW4CuWuy4pEfrG_AwtPFxugReLsshapel3I7QPMSj5c9t8noUNbSlIuR1cDe3xz5y0mWmIGusqLWHY5Fx1c8A",
    //    "token_type": "Bearer",
    //    "expires_in": 36000
    //}
    //
    //SStorage: id_token (from json)


}
