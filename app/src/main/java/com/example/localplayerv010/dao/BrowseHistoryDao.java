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

    // 插入或更新浏览记录
    @Insert
    long insertHistory(BrowseHistory history);

    // 更新浏览记录（观看时长、位置等）
    @Update
    int updateHistory(BrowseHistory history);

    // 获取用户的浏览记录（按时间倒序）
    @Query("SELECT * FROM browse_history WHERE userId = :userId ORDER BY watchTime DESC LIMIT :limit")
    List<BrowseHistory> getUserHistory(int userId, int limit);

    // 获取用户最近100条浏览记录
    @Query("SELECT * FROM browse_history WHERE userId = :userId ORDER BY watchTime DESC LIMIT 100")
    List<BrowseHistory> getRecentHistory(int userId);

    // 检查是否已存在该视频的浏览记录
    @Query("SELECT * FROM browse_history WHERE userId = :userId AND videoId = :videoId")
    BrowseHistory getHistoryByVideo(int userId, String videoId);

    // 删除超出100条的旧记录
    @Query("DELETE FROM browse_history WHERE id NOT IN " +
            "(SELECT id FROM browse_history WHERE userId = :userId ORDER BY watchTime DESC LIMIT 100)")
    int cleanOldHistory(int userId);

    // 统计各类视频数量
    @Query("SELECT category, COUNT(*) as count FROM browse_history " +
            "WHERE userId = :userId GROUP BY category ORDER BY count DESC")
    List<CategoryCount> getCategoryStats(int userId);

    // 获取总观看视频数量
    @Query("SELECT COUNT(*) FROM browse_history WHERE userId = :userId")
    int getTotalWatchedCount(int userId);

    // 删除单条浏览记录
    @Query("DELETE FROM browse_history WHERE id = :id")
    int deleteHistory(int id);

    // 清空用户浏览记录
    @Query("DELETE FROM browse_history WHERE userId = :userId")
    int clearUserHistory(int userId);

    // 静态内部类用于分类统计
    class CategoryCount {
        public String category;
        public int count;
    }
}