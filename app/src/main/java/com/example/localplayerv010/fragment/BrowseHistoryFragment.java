package com.example.localplayerv010.fragment;

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

import com.example.localplayerv010.R;
import com.example.localplayerv010.adapter.BrowseHistoryAdapter;
import com.example.localplayerv010.dao.BrowseHistoryDao;
import com.example.localplayerv010.model.BrowseHistory;
import com.example.localplayerv010.service.BrowseHistoryService;
import com.example.localplayerv010.service.UserPrefs;

import java.util.List;

public class BrowseHistoryFragment extends Fragment {
    private static final String TAG = "BrowseHistory";

    private RecyclerView recyclerView;
    private TextView tvStats, tvEmpty, tvTotalWatched;
    private BrowseHistoryService historyService;
    private BrowseHistoryAdapter adapter;  // 添加适配器

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_history, container, false);

        // 初始化所有视图
        recyclerView = view.findViewById(R.id.rv_browse_history);
        tvStats = view.findViewById(R.id.tv_category_stats);
        tvEmpty = view.findViewById(R.id.tv_empty_history);
        tvTotalWatched = view.findViewById(R.id.tv_total_watched);

        Log.d(TAG, "视图初始化 - tvTotalWatched: " + (tvTotalWatched != null));

        historyService = new BrowseHistoryService(getContext());
        setupRecyclerView();  // 先设置RecyclerView
        loadHistoryAndStats();

        return view;
    }

    private void setupRecyclerView() {
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new BrowseHistoryAdapter();  // 创建适配器
            recyclerView.setAdapter(adapter);      // 设置适配器
            Log.d(TAG, "RecyclerView设置完成");
        } else {
            Log.e(TAG, "RecyclerView为null!");
        }
    }

    private void loadHistoryAndStats() {
        if (!UserPrefs.isLoggedIn(getContext())) {
            showEmptyState("请先登录查看浏览记录");
            updateTotalWatched(0);
            return;
        }

        Log.d(TAG, "开始加载浏览记录...");

        // 加载浏览记录
        historyService.getBrowseHistory(new BrowseHistoryService.HistoryCallback() {
            @Override
            public void onSuccess(List<BrowseHistory> history) {
                Log.d(TAG, "成功获取浏览记录: " + history.size() + "条");

                // 立即更新总观看数
                updateTotalWatched(history.size());

                if (history.isEmpty()) {
                    showEmptyState("暂无浏览记录");
                } else {
                    hideEmptyState();
                    // 更新RecyclerView - 这里之前缺失！
                    updateHistoryList(history);
                }

                // 然后加载分类统计
                loadCategoryStats();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "获取浏览记录失败: " + errorMessage);
                showEmptyState("加载失败: " + errorMessage);
                updateTotalWatched(0);
            }
        });
    }

    private void loadCategoryStats() {
        historyService.getCategoryStats(new BrowseHistoryService.StatsCallback() {
            @Override
            public void onSuccess(List<BrowseHistoryDao.CategoryCount> stats) {
                Log.d(TAG, "成功获取分类统计: " + (stats != null ? stats.size() : 0) + "个分类");
                updateStatsDisplay(stats);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "获取分类统计失败: " + errorMessage);
                tvStats.setText("统计加载失败");
            }
        });
    }

    private void updateHistoryList(List<BrowseHistory> history) {
        Log.d(TAG, "更新历史列表，记录数: " + history.size());

        if (adapter != null) {
            adapter.setHistoryList(history);
            Log.d(TAG, "适配器数据已更新");
        } else {
            Log.e(TAG, "适配器为null，无法更新列表!");
        }

        // 调试：打印前几条记录
        for (int i = 0; i < Math.min(history.size(), 3); i++) {
            BrowseHistory item = history.get(i);
            Log.d(TAG, "记录" + i + ": " + item.getVideoTitle() + " - " + item.getCategory());
        }
    }

    private void updateStatsDisplay(List<BrowseHistoryDao.CategoryCount> stats) {
        if (stats == null || stats.isEmpty()) {
            tvStats.setText("暂无观看统计");
            return;
        }

        StringBuilder statsText = new StringBuilder("观看统计:\n");
        final int finalTotalVideos = calculateTotalVideos(stats);

        for (BrowseHistoryDao.CategoryCount stat : stats) {
            statsText.append("• ").append(stat.category).append(": ").append(stat.count).append("个\n");
        }

        // 获取总记录数来完善统计信息
        historyService.getBrowseHistory(new BrowseHistoryService.HistoryCallback() {
            @Override
            public void onSuccess(List<BrowseHistory> history) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        int totalRecords = history.size();
                        statsText.append("\n观看视频: ").append(finalTotalVideos).append("个");
                        statsText.append("\n观看记录: ").append(totalRecords).append("次");
                        tvStats.setText(statsText.toString());
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        statsText.append("\n总计: ").append(finalTotalVideos).append("个视频");
                        tvStats.setText(statsText.toString());
                    });
                }
            }
        });
    }

    private int calculateTotalVideos(List<BrowseHistoryDao.CategoryCount> stats) {
        int total = 0;
        for (BrowseHistoryDao.CategoryCount stat : stats) {
            total += stat.count;
        }
        return total;
    }

    private void updateTotalWatched(int count) {
        Log.d(TAG, "准备更新总观看数为: " + count);

        if (tvTotalWatched != null) {
            tvTotalWatched.setText(String.valueOf(count));
            Log.d(TAG, "✅ 成功更新总观看数: " + count);
        } else {
            Log.e(TAG, "❌ tvTotalWatched为null，无法更新!");
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
}