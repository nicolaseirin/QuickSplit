package org.quicksplit.service;

import org.quicksplit.model.Login;
import org.quicksplit.model.Token;
import org.quicksplit.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface UserClient {

    @GET("users/{id}")
    Call<User> getUser(@Path("id") String id);

    @PUT("users/{id}")
    Call<User> editUser(@Path("id") String id, @Body User user);

    @POST("users")
    Call<User> createAccount(@Body User userIn);

    @POST("authentications")
    Call<Token> login(@Body Login login);

    @GET("users")
    Call<List<User>> getUsers();

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") String id);

    @POST("users/{id}/friends")
    Call<List<User>> getFriends(@Path("id") String id);
}
