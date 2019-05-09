package org.quicksplit.service;

import org.quicksplit.model.Login;
import org.quicksplit.model.Token;
import org.quicksplit.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserClient {

    @POST("users")
    Call<User> createAccount(@Body User userIn);

    @POST("authentications")
    Call<Token> login(@Body Login login);

    @GET("users")
    Call<List<User>> getUsers();
}
