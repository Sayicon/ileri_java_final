package com.tbl324.mobile.api;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static final String TAG = "ApiClient";

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
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Log.d(TAG, "--> " + request.method() + " " + request.url());
                    Response response = chain.proceed(request);
                    int code = response.code();
                    ResponseBody body = response.body();
                    if (body != null) {
                        BufferedSource source = body.source();
                        source.request(Long.MAX_VALUE);
                        Buffer buffer = source.getBuffer().clone();
                        String bodyStr = buffer.readUtf8();
                        Log.d(TAG, "<-- " + code + " " + request.url());
                        Log.d(TAG, "Body: " + bodyStr.substring(0, Math.min(bodyStr.length(), 800)));
                    } else {
                        Log.d(TAG, "<-- " + code + " (no body)");
                    }
                    return response;
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
