package org.quicksplit.service;

import org.quicksplit.model.UserModelIn;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserClient {

    @POST("users")
    Call<UserModelIn> createAccount(@Body UserModelIn userIn);
}
