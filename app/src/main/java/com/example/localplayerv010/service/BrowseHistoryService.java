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

    // Record browsing history
    public void recordBrowse(VideoItem video) {
        executor.execute(() -> {
            try {
                int userId = UserPrefs.getCurrentUserId(context);

                if (userId == -1) {
                    Log.d("BrowseHistory", "The user is not logged in; browsing history will be skipped.");
                    return;
                }

               // Check if the video already exists.
                BrowseHistory existingHistory = historyDao.getHistoryByVideo(userId, video.getVideoId());

                if (existingHistory != null) {
                    // Update existing records
                    existingHistory.setWatchTime(new java.util.Date());
                    existingHistory.setLastPosition(video.getLastPlayPosition());
                    existingHistory.setWatchDuration(existingHistory.getWatchDuration() + 10000);
                    historyDao.updateHistory(existingHistory);
                    Log.d("BrowseHistory", "Update browsing history: " + video.getTitle());
                } else {
                    // Create a new record - save complete video information
                    BrowseHistory history = new BrowseHistory(
                            userId,
                            video.getVideoId(),
                            video.getTitle(),

                            video.getCategory()

                    );
                    history.setLastPosition(video.getLastPlayPosition());


                    long id = historyDao.insertHistory(history);
                    Log.d("BrowseHistory", "Add browsing history: " + video.getTitle() + ", ID: " + id);

                    // Clean up more than 100 old records
                    historyDao.cleanOldHistory(userId);
                }
            } catch (Exception e) {
                Log.e("BrowseHistory", "Failed to record browsing history: " + e.getMessage());
            }
        });
    }



    // 获取用户浏览记录
    public void getBrowseHistory(HistoryCallback callback) {
        executor.execute(() -> {
            try {
                int userId = UserPrefs.getCurrentUserId(context);
                if (userId == -1) {
                    callback.onFailure("User not logged in");
                    return;
                }

                List<BrowseHistory> history = historyDao.getRecentHistory(userId);
                callback.onSuccess(history);
            } catch (Exception e) {
                callback.onFailure("Failed to retrieve browsing history: " + e.getMessage());
            }
        });
    }

    // Get category statistics
    public void getCategoryStats(StatsCallback callback) {
        executor.execute(() -> {
            try {
                int userId = UserPrefs.getCurrentUserId(context);
                if (userId == -1) {
                    callback.onFailure("User not logged in");
                    return;
                }

                List<BrowseHistoryDao.CategoryCount> stats = historyDao.getCategoryStats(userId);
                callback.onSuccess(stats);
            } catch (Exception e) {
                callback.onFailure("Statistics failed to be retrieved: " + e.getMessage());
            }
        });
    }

    // Clear browsing history
    public void clearHistory(ClearCallback callback) {
        executor.execute(() -> {
            try {
                int userId = UserPrefs.getCurrentUserId(context);
                if (userId == -1) {
                    callback.onFailure("User not logged in");
                    return;
                }

                int deletedCount = historyDao.clearUserHistory(userId);
                callback.onSuccess(deletedCount);
            } catch (Exception e) {
                callback.onFailure("Clearing records failed: " + e.getMessage());
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