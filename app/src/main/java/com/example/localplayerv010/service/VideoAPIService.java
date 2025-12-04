package com.example.localplayerv010.service;

import android.util.Log;

import com.example.localplayerv010.model.PexelsVideo;
import com.example.localplayerv010.model.VideoItem;
import com.example.localplayerv010.model.VideoListResponse;
import com.example.localplayerv010.network.RetrofitClient;
import com.example.localplayerv010.utils.VideoConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VideoAPIService {


    public static void getHomeVideo(int page, int perPage, VideoLoadCallback callback) {
        // Instead of searching with empty strings, use the popular video API
        RetrofitClient.getPexelsApiService().getPopularVideos(page, perPage)
                .enqueue(new Callback<VideoListResponse>() {
                    @Override
                    public void onResponse(Call<VideoListResponse> call, Response<VideoListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<PexelsVideo> pexelsVideos = response.body().getVideos();
                            List<VideoItem> videoItems = VideoConverter.fromPexelsVideos(pexelsVideos);
                            callback.onSuccess(videoItems);
                            Log.d("VideoAPIService", "Successfully obtained page " + page + ", " + videoItems.size() + " videos");
                        } else {
                            Log.e("VideoAPIService", "API response failed, status code: " + response.code());
                            callback.onFailure("API response failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoListResponse> call, Throwable t) {
                        Log.e("VideoAPIService", "Network request failed: " + t.getMessage());
                        callback.onFailure("Network request failed: " + t.getMessage());
                    }
                });
    }

    public static void getHomeVideo(VideoLoadCallback callback) {
        getHomeVideo(1, 20, callback); // set page 1 as default
    }



    public static void getPopularVideos(VideoLoadCallback callback) {
        RetrofitClient.getPexelsApiService().getPopularVideos(1, 20) // Page 1, 20 per page
                .enqueue(new Callback<VideoListResponse>() {
                    @Override
                    public void onResponse(Call<VideoListResponse> call, Response<VideoListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<PexelsVideo> pexelsVideos = response.body().getVideos();
                            List<VideoItem> videoItems = VideoConverter.fromPexelsVideos(pexelsVideos);
                            callback.onSuccess(videoItems);
                        } else {
                            callback.onFailure("API response failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoListResponse> call, Throwable t) {
                        callback.onFailure("Network request failed: " + t.getMessage());
                    }
                });
    }

    public static void searchVideos(String query, int page, int perPage, VideoLoadCallback callback){
        RetrofitClient.getPexelsApiService().searchVideos(query,page,perPage).enqueue(new Callback<VideoListResponse>() {
            @Override
            public void onResponse(Call<VideoListResponse> call, Response<VideoListResponse> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    List<PexelsVideo>pexelsVideos = response.body().getVideos();
                    List<VideoItem>videoItems = VideoConverter.fromPexelsVideos(pexelsVideos);
                    callback.onSuccess(videoItems);

                }
                else {
                    callback.onFailure("API response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<VideoListResponse> call, Throwable t) {
                callback.onFailure("Network request failed: " + t.getMessage());
            }
        });
    }

    public static void searchVideos(String query, VideoLoadCallback callback) {
        searchVideos(query, 1, 20, callback);
    }



    public interface VideoLoadCallback {
        void onSuccess(List<VideoItem> videos);
        void onFailure(String errorMessage);
    }

    public static boolean testNetwork() {
        return RetrofitClient.testConnection();
    }
}
