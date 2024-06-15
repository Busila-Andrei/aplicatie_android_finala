package com.example.aplicatie_android_finala.config;

import android.util.Log;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.1.136:9080/";
    private static Retrofit retrofit = null;
    private static final String TAG = "RetrofitClient";

    public static ApiService getApiService() {
        if (retrofit == null) {
            Log.d(TAG, "Initializing Retrofit client");
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        Log.d(TAG, "Retrofit client initialized successfully");
        return retrofit.create(ApiService.class);
    }
}
