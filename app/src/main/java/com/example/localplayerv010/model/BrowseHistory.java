package com.example.localplayerv010.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import com.example.localplayerv010.database.DateConverter;

@Entity(tableName = "browse_history")
@TypeConverters(DateConverter.class)
public class BrowseHistory {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;
    @NonNull
    private String videoId;
    private String videoTitle;
    private String category;
    private long watchDuration; // 观看时长（毫秒）
    private Date watchTime;     // 观看时间
    private long lastPosition;  // 最后观看位置

    public BrowseHistory(int userId, String videoId, String videoTitle, String category) {
        this.userId = userId;
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.category = category;
        this.watchTime = new Date();
        this.watchDuration = 0;
        this.lastPosition = 0;
    }

    // Getter 和 Setter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getVideoTitle() { return videoTitle; }
    public void setVideoTitle(String videoTitle) { this.videoTitle = videoTitle; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getWatchDuration() { return watchDuration; }
    public void setWatchDuration(long watchDuration) { this.watchDuration = watchDuration; }

    public Date getWatchTime() { return watchTime; }
    public void setWatchTime(Date watchTime) { this.watchTime = watchTime; }

    public long getLastPosition() { return lastPosition; }
    public void setLastPosition(long lastPosition) { this.lastPosition = lastPosition; }
}
