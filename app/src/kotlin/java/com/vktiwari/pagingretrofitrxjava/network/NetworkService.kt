package com.vktiwari.pagingretrofitrxjava.network

import com.vktiwari.pagingretrofitrxjava.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkService private constructor() {

    private var retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(OkHttpClient().newBuilder()
                        .addNetworkInterceptor(HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.BODY))
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .build())
                .build()

    }

    companion object {
        private var sNetworkService: NetworkService? = null;
        public fun getInstance(): NetworkService {
            if (sNetworkService == null) {
                synchronized(NetworkService::class.java) {
                    sNetworkService = NetworkService()
                }
            }
            return this.sNetworkService!!
        }
    }


    fun <T> getRetrofitService(service: Class<T>): T {
        return sNetworkService!!.retrofit.create(service)
    }
}