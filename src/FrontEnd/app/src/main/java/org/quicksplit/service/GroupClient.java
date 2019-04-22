package org.quicksplit.service;

import org.quicksplit.model.GroupModelIn;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GroupClient {

    @POST("groups")
    Call<GroupModelIn> createAccount(@Body GroupModelIn userIn);

}






