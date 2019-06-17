package org.quicksplit.service;

import org.quicksplit.models.ModifyPurchase;
import org.quicksplit.models.Purchase;
import org.quicksplit.models.PurchaseModelIn;
import org.quicksplit.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PurchaseClient {

    @GET("purchases")
    Call<List<Purchase>> getPurchases();

    @GET("purchases/{id}")
    Call<PurchaseModelIn> getPurchases(@Path("id") String id);

    @GET("purchases/{id}/users")
    Call<List<User>> getParticipants(@Path("id") String id);

    @POST("purchases")
    Call<PurchaseModelIn> createPurchase(@Body Purchase purchase);

    @GET("purchases/{id}/image")
    Call<ResponseBody> getPurchaseImage(@Path("id") String id);

    @PUT("purchases/{id}")
    Call<PurchaseModelIn> modifyPurchase(@Path("id") String id, @Body ModifyPurchase purchase);

    @Multipart
    @POST("purchases/{id}/image")
    Call<ResponseBody> setPurchaseImage(@Path("id") String id, @Part MultipartBody.Part filePart);
}
