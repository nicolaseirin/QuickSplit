package org.quicksplit.service;

import org.quicksplit.model.Login;
import org.quicksplit.model.Token;
import org.quicksplit.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface UserClient {

    @GET
    Call<User> getUser(@Url String userId);

    @POST("users")
    Call<User> createAccount(@Body User userIn);

    @POST("authentications")
    Call<Token> login(@Body Login login);
}
