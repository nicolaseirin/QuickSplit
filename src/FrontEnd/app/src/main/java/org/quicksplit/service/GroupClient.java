package org.quicksplit.service;

import org.quicksplit.models.DebtorDebtee;
import org.quicksplit.models.Group;
import org.quicksplit.models.GroupModelIn;
import org.quicksplit.models.LeaveGroup;
import org.quicksplit.models.Purchase;
import org.quicksplit.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GroupClient {

    @GET("groups/{id}")
    Call<GroupModelIn> getGroup(@Path("id") String id);

    @POST("groups")
    Call<GroupModelIn> createGroup(@Body Group group);

    @PUT("groups/{id}")
    Call<GroupModelIn> modifyGroup(@Path("id") String id, @Body Group group);

    @DELETE("groups/{id}")
    Call<Void> deleteGroup(@Path("id") String id);

    @PUT("groups/leave")
    Call<Void> leaveGroup(@Body LeaveGroup leaveGroup);

    @GET("groups/{id}/users")
    Call<List<User>> getGroupMembers(@Path("id") String id);

    @GET("groups/{id}/purchases")
    Call<List<Purchase>> getPurchases(@Path("id") String id);

    @POST("groups/{id}/purchases")
    Call<Purchase> addPurchase(@Path("id") String id);

    @GET("groups/{id}/reports")
    Call<List<DebtorDebtee>> getSplitReport(@Path("id") String id);

    @GET("groups/{id}/reports")
    Call<List<DebtorDebtee>> getSplitReport(@Path("id") String id, @Query("currency") String currency);
}






