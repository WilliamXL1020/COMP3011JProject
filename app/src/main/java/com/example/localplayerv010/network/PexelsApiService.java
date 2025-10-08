package com.example.localplayerv010.network;

import com.example.localplayerv010.model.PexelsVideo;
import com.example.localplayerv010.model.VideoListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PexelsApiService {

    @GET("videos/popular")
    Call<VideoListResponse> getPopularVideos(
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    @GET("videos/search")
    Call<VideoListResponse> searchVideos(
            @Query("query") String query,
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    @GET("/videos/{id}")
    Call<PexelsVideo> getVideoById(
            @Path("id") int videoId
    );
}
