package com.tbl324.mobile.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static ApiClient instance;
    private final ApiService service;
    private String token;

    private ApiClient() {
        OkHttpClient http = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request.Builder req = chain.request().newBuilder();
                    if (token != null) req.addHeader("Authorization", "Bearer " + token);
                    return chain.proceed(req.build());
                })
                .build();

        service = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(http)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) instance = new ApiClient();
        return instance;
    }

    public void setToken(String token) { this.token = token; }
    public ApiService getService()     { return service; }
}
