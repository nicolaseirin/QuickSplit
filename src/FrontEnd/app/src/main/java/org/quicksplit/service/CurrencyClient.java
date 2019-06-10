package org.quicksplit.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CurrencyClient {

    @GET("currencies")
    Call<List<String>> getCurrencies();
}
