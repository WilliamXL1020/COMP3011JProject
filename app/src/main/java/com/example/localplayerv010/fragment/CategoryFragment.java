package com.example.localplayerv010.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.localplayerv010.Player.PlayerActivity;
import com.example.localplayerv010.R;
import com.example.localplayerv010.adapter.videoHotAdapter;
import com.example.localplayerv010.adapter.videoRecyclerAdapter;
import com.example.localplayerv010.model.VideoItem;
import com.example.localplayerv010.service.MockVideoService;
import com.example.localplayerv010.utils.RefreshUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private videoHotAdapter adapter;
    private List<VideoItem> categoryVideos = new ArrayList<>();
    private String categoryName;



    public static CategoryFragment newInstance(String categoryName) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString("category_name", categoryName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取分区名称
        if (getArguments() != null) {
            categoryName = getArguments().getString("category_name", "未知分区");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple, container, false);
        recyclerView = view.findViewById(R.id.rv_video_list);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        setupRecyclerView();
        loadCategoryData();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RefreshUtils.setupRefresh(swipeRefresh, this::refreshCategoryData);
    }

    private void setupRecyclerView() {
        // 单列布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // 使用新的横向布局适配器
        adapter = new videoHotAdapter(categoryVideos);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((position, video) -> {
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra("video_data", video);
            startActivity(intent);
        });
    }

    private void loadCategoryData() {
        List<VideoItem> allVideos = MockVideoService.getHomeVideo();
        categoryVideos = filterVideosByCategory(allVideos, categoryName);
        adapter.setVideoList(categoryVideos);
    }
    private List<VideoItem> filterVideosByCategory(List<VideoItem> allVideos, String category) {
        List<VideoItem> result = new ArrayList<>();

        // 就做最简单的筛选，其他逻辑都去掉
        for (VideoItem video : allVideos) {
            if (category.equals(video.getCategory())) {
                result.add(video);
            }
        }

        // 现在不做任何特殊处理，等明确需求后再添加
        return result;
    }


    private void refreshCategoryData() {
        new Handler().postDelayed(() -> {
            List<VideoItem> allVideos = MockVideoService.getHomeVideo();
            List<VideoItem> newVideos = filterVideosByCategory(allVideos, categoryName);

            Collections.shuffle(newVideos);
            adapter.setVideoList(newVideos);
            RefreshUtils.stopRefresh(swipeRefresh);
            Toast.makeText(getContext(), categoryName + "视频已更新", Toast.LENGTH_SHORT).show();
        }, 300);
    }
}