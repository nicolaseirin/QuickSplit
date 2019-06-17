package org.quicksplit.service;

import org.quicksplit.cache.Cacheable;
import org.quicksplit.models.Group;
import org.quicksplit.models.GroupModelIn;
import org.quicksplit.models.Login;
import org.quicksplit.models.Purchase;
import org.quicksplit.models.PurchaseModelIn;
import org.quicksplit.models.Token;
import org.quicksplit.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @GET("users/{id}/friends")
    Call<List<User>> getFriends(@Path("id") String id);

    @POST("users/{id}/friends/{idFriend}")
    Call<Void> addFriend(@Path("id") String id, @Path("idFriend") String idFriend);

    @DELETE("users/{id}/friends/{idFriend}")
    Call<Void> deleteFriend(@Path("id") String id, @Path("idFriend") String idFriend);

    @GET("users")
    Call<List<User>> friendsLookup(@Query("excludeFriendsOfId") String id, @Query("find") String like);

    @GET("users/{id}/avatars")
    Call<ResponseBody> getUserAvatar(@Path("id") String id);

    @POST("users/{id}/avatars")
    Call<Void> setUserAvatar(@Path("id") String id, @Body String avatarUrl);

    @Multipart
    @POST("users/{id}/avatars")
    Call<ResponseBody> setUserAvatar(@Path("id") String id, @Part MultipartBody.Part filePart);

    @Cacheable
    @GET("users/{id}/groups")
    Call<List<GroupModelIn>> getUserGroups(@Path("id") String id);

    @GET("users/{id}/purchases")
    Call<List<PurchaseModelIn>> getUserPurchases(@Path("id") String id);
}
