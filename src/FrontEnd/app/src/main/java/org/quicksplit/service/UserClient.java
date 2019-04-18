package org.quicksplit.service;

import org.quicksplit.model.Login;
import org.quicksplit.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserClient {

    @POST("users")
    Call<User> createAccount(@Body User userIn);

    @POST("authentications")
    Call<User> login(@Body Login login);
}
