package org.quicksplit.service;

import org.quicksplit.models.Group;
import org.quicksplit.models.LeaveGroup;
import org.quicksplit.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GroupClient {

    @GET("groups")
    Call<List<Group>> getAllGroups();

    @GET("groups/{id}")
    Call<Group> getGroup(@Path("id") String id);

    @GET("groups/{id}/users")
    Call<List<User>> getGroupMembers(@Path("id") String id);

    @POST("groups")
    Call<Group> createGroup(@Body Group groupIn);

    @PUT("groups/{id}")
    Call<Group> modifyGroup(@Path("id") String id);

    @PUT("groups/leave")
    Call<Void> leaveGroup(@Body LeaveGroup leaveGroup);

    @DELETE("groups/{id}")
    Call<Void> deleteGroup(@Path("id") String id);
}






