package org.quicksplit.service;

import org.quicksplit.models.Group;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GroupClient {

    @POST("groups")
    Call<Group> createGroup(@Body Group groupIn);

    @DELETE("groups/{id}")
    Call<Void> deleteGroup(@Path("id") String id);

    @PUT("groups/{id}")
    Call<Group> modifyGroup(@Path("id") String id);

}






