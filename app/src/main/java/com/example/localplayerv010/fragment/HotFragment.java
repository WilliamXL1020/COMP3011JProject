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
        loadHotData();
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

    private void loadHotData() {
        List<VideoItem> allVideos = MockVideoService.getHomeVideo();
        hotVideos = processHotData(allVideos);
        adapter.setVideoList(hotVideos);
    }
    private void refreshHotData() {
        // 完全个性化的业务逻辑
        new Handler().postDelayed(() -> {
            List<VideoItem> newVideos = processHotData(MockVideoService.getHomeVideo());
            adapter.setVideoList(newVideos);
            RefreshUtils.stopRefresh(swipeRefresh);
            Toast.makeText(getContext(), "热搜已更新", Toast.LENGTH_SHORT).show();
        }, 300);
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