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
    private videoHotAdapter adapter; // 使用新的适配器
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

        // 一行代码，传入刷新时要执行的逻辑
        RefreshUtils.setupRefresh(swipeRefresh, this::refreshHotData);
    }

    private void setupRecyclerView() {
        // 单列布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // 使用新的横向布局适配器
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
                hotVideos = videos; // 直接使用API返回的热门视频，不需要额外处理
                adapter.setVideoList(hotVideos);
                Log.d("HotFragment", "成功加载 " + hotVideos.size() + " 个热门视频");
            }

            @Override
            public void onFailure(String errorMessage) {
                // 网络失败，使用备用数据
                List<VideoItem> fallbackVideos = MockVideoService.getHomeVideo();
                adapter.setVideoList(fallbackVideos);

                Toast.makeText(getContext(), "热门数据加载失败，使用本地数据", Toast.LENGTH_SHORT).show();
                Log.e("HotFragment", "加载失败: " + errorMessage);
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
                Toast.makeText(getContext(), "热门视频已更新", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                List<VideoItem> fallbackVideos = MockVideoService.getHomeVideo();
                adapter.setVideoList(fallbackVideos);
                RefreshUtils.stopRefresh(swipeRefresh);
                Toast.makeText(getContext(), "更新失败: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<VideoItem> processHotData(List<VideoItem> allVideos) {
        List<VideoItem> result = new ArrayList<>();
        Log.d("VideoProcess", "原始数据: " + allVideos.size() + "个视频");



        // 1. 限制数量（比如20个）
        int maxCount = Math.min(allVideos.size(), 20);
        for (int i = 0; i < maxCount; i++) {
            result.add(allVideos.get(i));
        }

        Log.d("VideoProcess", "最终返回: " + result.size() + "个视频");
        return result;
    }
}