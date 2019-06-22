package org.quicksplit;

import org.quicksplit.cache.GsonCacheableConverter;
import org.quicksplit.cache.GsonResponseListener;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator implements GsonResponseListener {

    private static int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static File cacheDir = new File("cache", "HttpCache");
    private static final String BASE_URL = "http://192.168.1.6:5000/api/";

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create());

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonCacheableConverter.create(this))
                .build();
    }

    private static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder()
                    .cache(new Cache(cacheDir, cacheSize));

    public static <S> S createService(
            Class<S> serviceClass) {

        builder.client(httpClient.connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build());

        retrofit = builder.build();


        return retrofit.create(serviceClass);
    }

    public  <S> S createServiceNs(
            Class<S> serviceClass, final String authToken) {
        if (!authToken.isEmpty()) {
            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit = createRetrofit();
            }
        }

        return retrofit.create(serviceClass);
    }

    public static <S> S createService(
            Class<S> serviceClass, final String authToken) {
        if (!authToken.isEmpty()) {
            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        return retrofit.create(serviceClass);
    }

    public static Retrofit retrofit() {
        return retrofit;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public void onCacheableResponse(Class type, Object responseBody) {
        if (responseBody instanceof Collection) {

        } else {

        }
    }
}
