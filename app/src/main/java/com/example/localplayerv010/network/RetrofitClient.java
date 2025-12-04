package com.example.localplayerv010.network;

import android.util.Log;

import com.example.localplayerv010.BuildConfig;
import com.example.localplayerv010.model.VideoListResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String Base_URL="https://api.pexels.com/";
    private static final String API_KEY = "Wa8C16ki35YNkzRd85OCzfx9p73a0aMSfspwaq52WwdJwlcEk0F8ZhZm";

    private static Retrofit retrofit;

    private static PexelsApiService pexelsApiService;

    public static PexelsApiService getPexelsApiService(){
        if (pexelsApiService == null) {
            pexelsApiService = getRetrofit().create(PexelsApiService.class);
        }
        return pexelsApiService;
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // Add an authentication interceptor - automatically add an API key for each request
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    // Create a new request and add authentication headers.
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", API_KEY);

                    Request newRequest = requestBuilder.build();
                    return chain.proceed(newRequest);
                }
            });

            // Add a log interceptor (in debug mode only)
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClient.addInterceptor(logging);
            }

            // 3. Set timeout period
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.writeTimeout(30, TimeUnit.SECONDS);

            // 4. Create a Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(Base_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public static boolean testConnection() {
        try {
            retrofit2.Response<VideoListResponse> response = getPexelsApiService().getPopularVideos(1, 1).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            Log.e("RetrofitClient", "Network test failed: " + e.getMessage());
            return false;
        }
    }
}
