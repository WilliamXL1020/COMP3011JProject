package com.example.localplayerv010.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.localplayerv010.Player.PlayerActivity;
import com.example.localplayerv010.R;
import com.example.localplayerv010.adapter.BrowseHistoryAdapter;
import com.example.localplayerv010.dao.BrowseHistoryDao;
import com.example.localplayerv010.model.BrowseHistory;
import com.example.localplayerv010.model.VideoItem;
import com.example.localplayerv010.service.BrowseHistoryService;
import com.example.localplayerv010.service.MockVideoService;
import com.example.localplayerv010.service.VideoAPIService;
import com.example.localplayerv010.service.UserPrefs;

import java.util.List;

public class BrowseHistoryFragment extends Fragment {
    private static final String TAG = "BrowseHistory";

    private RecyclerView recyclerView;
    private TextView tvStats, tvEmpty, tvTotalWatched, tvClearHistory;
    private BrowseHistoryService historyService;
    private BrowseHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_history, container, false);

        initViews(view);
        historyService = new BrowseHistoryService(requireContext());
        setupRecyclerView();
        loadHistoryAndStats();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_browse_history);
        tvStats = view.findViewById(R.id.tv_category_stats);
        tvEmpty = view.findViewById(R.id.tv_empty_history);
        tvTotalWatched = view.findViewById(R.id.tv_total_watched);
        tvClearHistory = view.findViewById(R.id.tv_clear_history);

        Log.d(TAG, "视图初始化完成");
    }

    private void setupRecyclerView() {
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new BrowseHistoryAdapter();

            // 设置item点击事件 - 从头播放
            adapter.setOnItemClickListener((position, history) -> {
                Log.d(TAG, "点击视频: " + history.getVideoTitle());
                playVideoFromHistory(history, 0); // 从头开始播放
            });

            // 设置续看点击事件 - 从上次位置播放
            adapter.setOnContinueWatchClickListener((position, history) -> {
                Log.d(TAG, "续看视频: " + history.getVideoTitle() + ", 位置: " + history.getLastPosition());
                playVideoFromHistory(history, history.getLastPosition());
            });

            recyclerView.setAdapter(adapter);
            Log.d(TAG, "RecyclerView设置完成");
        }
    }

    private void setupClickListeners() {
        if (tvClearHistory != null) {
            tvClearHistory.setOnClickListener(v -> clearHistory());
        }
    }

    private void loadHistoryAndStats() {
        if (!UserPrefs.isLoggedIn(requireContext())) {
            showEmptyState("请先登录查看浏览记录");
            updateTotalWatched(0);
            return;
        }

        Log.d(TAG, "开始加载浏览记录...");

        // 先加载浏览记录
        historyService.getBrowseHistory(new BrowseHistoryService.HistoryCallback() {
            @Override
            public void onSuccess(List<BrowseHistory> history) {
                Log.d(TAG, "成功获取浏览记录: " + history.size() + "条");

                requireActivity().runOnUiThread(() -> {
                    // 立即更新总观看数
                    updateTotalWatched(history.size());

                    if (history.isEmpty()) {
                        showEmptyState("暂无浏览记录");
                    } else {
                        hideEmptyState();
                        // 更新RecyclerView
                        updateHistoryList(history);
                    }

                    // 然后加载分类统计
                    loadCategoryStats(history.size());
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "获取浏览记录失败: " + errorMessage);
                requireActivity().runOnUiThread(() -> {
                    showEmptyState("加载失败: " + errorMessage);
                    updateTotalWatched(0);
                });
            }
        });
    }

    private void loadCategoryStats(final int totalRecords) {
        historyService.getCategoryStats(new BrowseHistoryService.StatsCallback() {
            @Override
            public void onSuccess(List<BrowseHistoryDao.CategoryCount> stats) {
                Log.d(TAG, "成功获取分类统计: " + (stats != null ? stats.size() : 0) + "个分类");
                requireActivity().runOnUiThread(() -> {
                    updateStatsDisplay(stats, totalRecords);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "获取分类统计失败: " + errorMessage);
                requireActivity().runOnUiThread(() -> {
                    tvStats.setText("统计加载失败\n\n总观看记录: " + totalRecords + "次");
                });
            }
        });
    }

    private void updateHistoryList(List<BrowseHistory> history) {
        Log.d(TAG, "更新历史列表，记录数: " + history.size());

        if (adapter != null) {
            adapter.setHistoryList(history);
            Log.d(TAG, "适配器数据已更新");
        }
    }

    private void updateStatsDisplay(List<BrowseHistoryDao.CategoryCount> stats, final int totalRecords) {
        if (stats == null || stats.isEmpty()) {
            tvStats.setText("暂无观看统计\n\n总观看记录: " + totalRecords + "次");
            return;
        }

        StringBuilder statsText = new StringBuilder("观看统计:\n");
        final int[] totalVideos = {0};

        for (BrowseHistoryDao.CategoryCount stat : stats) {
            statsText.append("• ").append(stat.category).append(": ").append(stat.count).append("个\n");
            totalVideos[0] += stat.count;
        }

        statsText.append("\n观看视频: ").append(totalVideos[0]).append("个");
        statsText.append("\n观看记录: ").append(totalRecords).append("次");
        tvStats.setText(statsText.toString());
    }

    private void playVideoFromHistory(BrowseHistory history, long startPosition) {
        Log.d(TAG, "准备播放视频: " + history.getVideoTitle() + ", 起始位置: " + startPosition);

        // 先尝试从API搜索视频
        VideoAPIService.searchVideos(history.getVideoTitle(), 1, 1, new VideoAPIService.VideoLoadCallback() {
            @Override
            public void onSuccess(List<VideoItem> videos) {
                if (videos != null && !videos.isEmpty()) {
                    VideoItem video = videos.get(0);
                    video.setLastPlayPosition(startPosition);
                    navigateToPlayer(video);
                } else {
                    // 如果API搜索失败，使用Mock数据
                    useMockVideoData(history, startPosition);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "搜索视频失败: " + errorMessage);
                // 使用Mock数据作为备选
                useMockVideoData(history, startPosition);
            }
        });
    }

    private void useMockVideoData(BrowseHistory history, long startPosition) {
        // 从Mock数据中查找匹配的视频
        List<VideoItem> allVideos = MockVideoService.getHomeVideo();
        VideoItem foundVideo = null;

        for (VideoItem video : allVideos) {
            if (video.getTitle().contains(history.getVideoTitle()) ||
                    history.getVideoTitle().contains(video.getTitle())) {
                foundVideo = video;
                break;
            }
        }

        if (foundVideo != null) {
            foundVideo.setLastPlayPosition(startPosition);
            navigateToPlayer(foundVideo);
        } else {
            // 如果都没找到，创建临时视频数据
            createTempVideoItem(history, startPosition);
        }
    }

    private void createTempVideoItem(BrowseHistory history, long startPosition) {
        VideoItem video = new VideoItem();
        video.setVideoId(history.getVideoId());
        video.setTitle(history.getVideoTitle());
        video.setCategory(history.getCategory());
        video.setDescription("来自浏览记录的视频");
        video.setLastPlayPosition(startPosition);

        // 设置一个默认的视频路径
        video.setVideoPath("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4");

        navigateToPlayer(video);
    }

    private void navigateToPlayer(VideoItem video) {
        try {
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra("video_data", video);
            startActivity(intent);
            Log.d(TAG, "成功跳转到播放页面");
        } catch (Exception e) {
            Log.e(TAG, "跳转到播放页面失败: " + e.getMessage());
            Toast.makeText(getContext(), "打开视频失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTotalWatched(int count) {
        Log.d(TAG, "更新总观看数为: " + count);

        if (tvTotalWatched != null) {
            tvTotalWatched.setText(String.valueOf(count));
        }
    }

    private void showEmptyState(String message) {
        if (tvEmpty != null) {
            tvEmpty.setText(message);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void hideEmptyState() {
        if (tvEmpty != null) {
            tvEmpty.setVisibility(View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void clearHistory() {
        if (!UserPrefs.isLoggedIn(requireContext())) {
            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        historyService.clearHistory(new BrowseHistoryService.ClearCallback() {
            @Override
            public void onSuccess(int deletedCount) {
                Toast.makeText(getContext(), "已清空 " + deletedCount + " 条记录", Toast.LENGTH_SHORT).show();
                // 清空后重新加载
                loadHistoryAndStats();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "清空失败: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}