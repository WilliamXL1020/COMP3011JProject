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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;



import com.example.localplayerv010.Player.PlayerActivity;
import com.example.localplayerv010.R;
import com.example.localplayerv010.adapter.bannerAdapter;
import com.example.localplayerv010.model.VideoItem;
import com.example.localplayerv010.service.MockVideoService;
import com.example.localplayerv010.adapter.videoRecyclerAdapter;
import com.example.localplayerv010.utils.RefreshUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecommendFragment extends Fragment {
    private RecyclerView recyclerView;
    private ViewPager2 bannerPager;
    private videoRecyclerAdapter adapter;
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private long AUTO_SCROLL_DELAY = 3000;
    private boolean isUserTouching = false;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        // 初始化轮播图（可选，可以先注释掉）
        bannerPager = view.findViewById(R.id.banner_pager);
        setupBanner();
        recyclerView = view.findViewById(R.id.rv_video_list);
        setupRecyclerView();

        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh);

        // 一行代码，传入刷新时要执行的逻辑
        RefreshUtils.setupRefresh(swipeRefresh, this::refreshRecommendData);
    }

    private void setupRecyclerView() {

        //设置好一个两列视频的布局
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        // 设置适配器
        List<VideoItem> allVideos = MockVideoService.getHomeVideo();
        List<VideoItem> displayVideos = processVideoData(allVideos);
        adapter = new videoRecyclerAdapter(displayVideos);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((position, video) -> {
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra("video_data", video);
            startActivity(intent);
        });
    }

    private List<VideoItem> processVideoData(List<VideoItem> allVideos) {
        List<VideoItem> result = new ArrayList<>();
        Log.d("VideoProcess", "原始数据: " + allVideos.size() + "个视频");

        // 1. 随机打乱
        Collections.shuffle(allVideos);

        // 2. 限制数量（比如20个）
        int maxCount = Math.min(allVideos.size(), 10);
        for (int i = 0; i < maxCount; i++) {
            result.add(allVideos.get(i));
        }

        Log.d("VideoProcess", "最终返回: " + result.size() + "个视频");
        return result;
    }




    private void setupBanner() {
        List<Integer> bannerImages = Arrays.asList(
                R.drawable.default_avatar,
                R.drawable.default_avatar,
                R.drawable.banner
        );
        bannerAdapter bannerAdapter = new bannerAdapter(bannerImages);
        bannerPager.setAdapter(bannerAdapter);

        bannerPager.setOffscreenPageLimit(3);


        bannerPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager2.SCROLL_STATE_DRAGGING:
                        // 用户开始拖动，停止自动轮播
                        isUserTouching = true;
                        stopAutoScroll();
                        break;
                    case ViewPager2.SCROLL_STATE_IDLE:
                        // 滚动停止，如果是用户操作结束就重新开始自动轮播
                        if (isUserTouching) {
                            isUserTouching = false;
                            startAutoScroll();
                        }
                        break;
                    case ViewPager2.SCROLL_STATE_SETTLING:
                        // 自动滚动中，不做处理
                        break;
                }
            }
        });
        startAutoScroll();

    }

    private void startAutoScroll() {
        stopAutoScroll();
        autoScrollHandler = new Handler();
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerPager.getAdapter() != null && bannerPager.getAdapter().getItemCount() > 0 &&
                        !isUserTouching) {
                    int currentItem = bannerPager.getCurrentItem();
                    int nextItem = (currentItem + 1) % bannerPager.getAdapter().getItemCount();
                    bannerPager.setCurrentItem(nextItem, true);
                }
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
    }

    private void resetAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
        }
    }

    private void stopAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }

    private void refreshRecommendData() {
        // 完全个性化的业务逻辑
        new Handler().postDelayed(() -> {
            List<VideoItem> newVideos = processVideoData(MockVideoService.getHomeVideo());
            adapter.setVideoList(newVideos);
            RefreshUtils.stopRefresh(swipeRefresh);
            Toast.makeText(getContext(), "推荐已更新", Toast.LENGTH_SHORT).show();
        }, 300);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 页面显示时开始轮播
        if (!isUserTouching) {
            startAutoScroll();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 页面隐藏时停止轮播，节省资源
        stopAutoScroll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 清理资源
        stopAutoScroll();
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacksAndMessages(null);
        }
    }




}

