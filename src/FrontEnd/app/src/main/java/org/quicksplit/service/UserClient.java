package org.quicksplit.service;

import org.quicksplit.models.Login;
import org.quicksplit.models.Token;
import org.quicksplit.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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

    @DELETE("users/{id}/friends{idFriend}")
    Call<Void> deleteFriend(@Path("id") String id, @Path("idFriend") String idFriend);
}
