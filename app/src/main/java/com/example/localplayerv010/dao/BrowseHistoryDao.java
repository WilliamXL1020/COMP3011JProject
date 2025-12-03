// BrowseHistoryDao.java
package com.example.localplayerv010.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.localplayerv010.model.BrowseHistory;

import java.util.List;

@Dao
public interface BrowseHistoryDao {

    // Insert or update browsing history
    @Insert
    long insertHistory(BrowseHistory history);

    // Update browsing history (viewing duration, location, etc.)
    @Update
    int updateHistory(BrowseHistory history);

    // Retrieve user browsing history (in reverse chronological order)
    @Query("SELECT * FROM browse_history WHERE userId = :userId ORDER BY watchTime DESC LIMIT :limit")
    List<BrowseHistory> getUserHistory(int userId, int limit);

    // Get the user's 100 most recent browsing records
    @Query("SELECT * FROM browse_history WHERE userId = :userId ORDER BY watchTime DESC LIMIT 100")
    List<BrowseHistory> getRecentHistory(int userId);

    // Check if the video already exists in your viewing history.
    @Query("SELECT * FROM browse_history WHERE userId = :userId AND videoId = :videoId")
    BrowseHistory getHistoryByVideo(int userId, String videoId);

    // Delete more than 100 old records.
    @Query("DELETE FROM browse_history WHERE id NOT IN " +
            "(SELECT id FROM browse_history WHERE userId = :userId ORDER BY watchTime DESC LIMIT 100)")
    int cleanOldHistory(int userId);

    // Statistics on the number of videos of various types
    @Query("SELECT category, COUNT(*) as count FROM browse_history " +
            "WHERE userId = :userId GROUP BY category ORDER BY count DESC")
    List<CategoryCount> getCategoryStats(int userId);

    // Get the total number of videos viewed
    @Query("SELECT COUNT(*) FROM browse_history WHERE userId = :userId")
    int getTotalWatchedCount(int userId);

    // Delete a single browsing record
    @Query("DELETE FROM browse_history WHERE id = :id")
    int deleteHistory(int id);

    // Clear user browsing history
    @Query("DELETE FROM browse_history WHERE userId = :userId")
    int clearUserHistory(int userId);

    // Static inner classes are used for classification and statistics
    class CategoryCount {
        public String category;
        public int count;
    }
}