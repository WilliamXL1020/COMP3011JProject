package com.example.localplayerv010.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.localplayerv010.Player.PlayerActivity;
import com.example.localplayerv010.R;
import com.example.localplayerv010.adapter.videoHotAdapter;
import com.example.localplayerv010.model.VideoItem;
import com.example.localplayerv010.service.MockVideoService;
import com.example.localplayerv010.service.VideoAPIService;
import com.example.localplayerv010.utils.RefreshUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HotFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private videoHotAdapter adapter; // Use the new adapter
    private List<VideoItem> hotVideos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hot, container, false);
        recyclerView = view.findViewById(R.id.rv_video_list);
        setupRecyclerView();
        loadPopularVideos();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh);

        // A single line of code, passing in the logic to be executed upon refresh
        RefreshUtils.setupRefresh(swipeRefresh, this::refreshHotData);
    }

    private void setupRecyclerView() {
        // Single column layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Use the new landscape layout adapter
        adapter = new videoHotAdapter(hotVideos);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((position, video) -> {
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra("video_data", video);
            startActivity(intent);
        });
    }




    private void loadPopularVideos() {
        VideoAPIService.getPopularVideos(new VideoAPIService.VideoLoadCallback() {
            @Override
            public void onSuccess(List<VideoItem> videos) {
                hotVideos = videos; // The popular videos returned by the API can be used directly without any additional processing
                adapter.setVideoList(hotVideos);
                Log.d("HotFragment", "successfully loaded " + hotVideos.size() + " popular videos");
            }

            @Override
            public void onFailure(String errorMessage) {
                // Network failure, using backup data
                List<VideoItem> fallbackVideos = MockVideoService.getHomeVideo();
                adapter.setVideoList(fallbackVideos);

                Toast.makeText(getContext(), "Popular data failed to load; local data is used instead", Toast.LENGTH_SHORT).show();
                Log.e("HotFragment", "load failure: " + errorMessage);
            }
        });
    }

    private void refreshHotData() {
         VideoAPIService.getPopularVideos(new VideoAPIService.VideoLoadCallback() {
            @Override
            public void onSuccess(List<VideoItem> videos) {
                hotVideos = videos;
                adapter.setVideoList(hotVideos);
                RefreshUtils.stopRefresh(swipeRefresh);
                Toast.makeText(getContext(), "Popular videos have been updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                List<VideoItem> fallbackVideos = MockVideoService.getHomeVideo();
                adapter.setVideoList(fallbackVideos);
                RefreshUtils.stopRefresh(swipeRefresh);
                Toast.makeText(getContext(), "Update failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<VideoItem> processHotData(List<VideoItem> allVideos) {
        List<VideoItem> result = new ArrayList<>();
        Log.d("VideoProcess", "Raw data: " + allVideos.size() + "videos");



        // Limited to 20
        int maxCount = Math.min(allVideos.size(), 20);
        for (int i = 0; i < maxCount; i++) {
            result.add(allVideos.get(i));
        }

        Log.d("VideoProcess", "finally returned: " + result.size() + "videos");
        return result;
    }
}