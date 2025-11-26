// BrowseHistoryService.java
package com.example.localplayerv010.service;

import android.content.Context;
import android.util.Log;

import com.example.localplayerv010.dao.BrowseHistoryDao;
import com.example.localplayerv010.database.AppDatabase;
import com.example.localplayerv010.model.BrowseHistory;
import com.example.localplayerv010.model.VideoItem;
import com.example.localplayerv010.service.UserPrefs;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BrowseHistoryService {
    private BrowseHistoryDao historyDao;
    private Executor executor;
    private Context context;
    private static final int MAX_HISTORY_COUNT = 100;

    public BrowseHistoryService(Context context) {
        this.context = context.getApplicationContext();
        AppDatabase database = AppDatabase.getInstance(context);
        this.historyDao = database.browseHistoryDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // 记录浏览历史
    public void recordBrowse(VideoItem video) {
        executor.execute(() -> {
            try {
                int userId = UserPrefs.getCurrentUserId(context);
                if (userId == -1) {
                    Log.d("BrowseHistory", "用户未登录，跳过记录浏览历史");
                    return;
                }

                // 检查是否已存在该视频的记录
                BrowseHistory existingHistory = historyDao.getHistoryByVideo(userId, video.getVideoId());

                if (existingHistory != null) {
                    // 更新现有记录
                    existingHistory.setWatchTime(new java.util.Date());
                    existingHistory.setLastPosition(video.getLastPlayPosition());
                    existingHistory.setWatchDuration(existingHistory.getWatchDuration() + 10000); // 假设每次观看增加10秒
                    historyDao.updateHistory(existingHistory);
                    Log.d("BrowseHistory", "更新浏览记录: " + video.getTitle());
                } else {
                    // 创建新记录
                    BrowseHistory history = new BrowseHistory(
                            userId,
                            video.getVideoId(),
                            video.getTitle(),
                            video.getCategory()
                    );
                    history.setLastPosition(video.getLastPlayPosition());
                    history.setWatchDuration(10000); // 初始观看时长10秒

                    long id = historyDao.insertHistory(history);
                    Log.d("BrowseHistory", "新增浏览记录: " + video.getTitle() + ", ID: " + id);

                    // 清理超出100条的旧记录
                    historyDao.cleanOldHistory(userId);
                }
            } catch (Exception e) {
                Log.e("BrowseHistory", "记录浏览历史失败: " + e.getMessage());
            }
        });
    }

    // 获取用户浏览记录
    public void getBrowseHistory(HistoryCallback callback) {
        executor.execute(() -> {
            try {
                int userId = UserPrefs.getCurrentUserId(context);
                if (userId == -1) {
                    callback.onFailure("用户未登录");
                    return;
                }

                List<BrowseHistory> history = historyDao.getRecentHistory(userId);
                callback.onSuccess(history);
            } catch (Exception e) {
                callback.onFailure("获取浏览记录失败: " + e.getMessage());
            }
        });
    }

    // 获取分类统计
    public void getCategoryStats(StatsCallback callback) {
        executor.execute(() -> {
            try {
                int userId = UserPrefs.getCurrentUserId(context);
                if (userId == -1) {
                    callback.onFailure("用户未登录");
                    return;
                }

                List<BrowseHistoryDao.CategoryCount> stats = historyDao.getCategoryStats(userId);
                callback.onSuccess(stats);
            } catch (Exception e) {
                callback.onFailure("获取统计失败: " + e.getMessage());
            }
        });
    }

    // 清空浏览记录
    public void clearHistory(ClearCallback callback) {
        executor.execute(() -> {
            try {
                int userId = UserPrefs.getCurrentUserId(context);
                if (userId == -1) {
                    callback.onFailure("用户未登录");
                    return;
                }

                int deletedCount = historyDao.clearUserHistory(userId);
                callback.onSuccess(deletedCount);
            } catch (Exception e) {
                callback.onFailure("清空记录失败: " + e.getMessage());
            }
        });
    }

    public interface HistoryCallback {
        void onSuccess(List<BrowseHistory> history);
        void onFailure(String errorMessage);
    }

    public interface StatsCallback {
        void onSuccess(List<BrowseHistoryDao.CategoryCount> stats);
        void onFailure(String errorMessage);
    }

    public interface ClearCallback {
        void onSuccess(int deletedCount);
        void onFailure(String errorMessage);
    }
}