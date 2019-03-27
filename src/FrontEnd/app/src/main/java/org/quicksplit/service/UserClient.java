package org.quicksplit.service;

import org.quicksplit.model.UserModelIn;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterUser {

    @POST("/users")
    @FormUrlEncoded
    Call<UserModelIn> createAccount(@Body UserModelIn userIn);
}
