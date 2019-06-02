package org.quicksplit.service;

import org.quicksplit.models.Purchase;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PurchaseClient {

    @GET("purchases")
    Call<List<Purchase>> getAllPurchases();

    @GET("purchases/{id}")
    Call<Purchase> getPurchase(@Path("id") String id);

    @POST("purchase")
    Call<Purchase> createPurchase(Purchase purchase);

    @PUT("purchase/{id}")
    Call<Purchase> modifyPurchase(@Path("id") String id, Purchase purchase);
}
