package com.vktiwari.pagingretrofitrxjava.network;

import android.content.Context;
import android.net.ConnectivityManager;


import com.vktiwari.pagingretrofitrxjava.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private Retrofit retrofit;
    private static NetworkService sNetworkService;

    private NetworkService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient().newBuilder()
                        .addNetworkInterceptor(new HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.BODY))
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15,TimeUnit.SECONDS)
                        .build())
        .       build();

    }

    public static NetworkService getInstance() {
        if (sNetworkService == null) {
            synchronized (NetworkService.class) {
                sNetworkService = new NetworkService();
            }
        }
        return sNetworkService;
    }

    public <T> T getRetrofitService(Class<T> service) {
        return sNetworkService.retrofit.create(service);
    }
}
