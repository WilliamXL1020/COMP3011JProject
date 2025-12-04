package com.example.localplayerv010.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.localplayerv010.utils.VideoUtils;

import java.util.Date;
import java.util.List;

public class VideoItem implements Parcelable {

    // 1. Basic Identification Information
    private String VideoId;          // Unique video ID
    private String Title;           // Video title
    private String Description;     // Video description
    private String ShortDescription; // Short description (for list display)

    // 2. Media File Information
    private String VideoPath;       // Local file path or network URL
    private long Duration;          // Video duration (milliseconds)
    private long FileSize;          // File size (bytes)
    private String Format;          // Video format: mp4, mkv, etc.
    private int ResolutionWidth;    // Resolution width
    private int ResolutionHeight;   // Resolution height

    // 3. Metadata Information
    private String Category;        // Category: game, music, film, etc.
    private List<String> Tags;      // List of tags
    private Date UploadTime;        // Upload time
    private String ThumbnailUrl;    // Thumbnail network URL
    private String ThumbnailPath;   // Thumbnail local path

    // 4. Statistical Information
    private int PlayCount;          // Play count
    private int LikeCount;          // Like count
    private int FavoriteCount;      // Favorite count
    private int CommentCount;       // Comment count

    // 5. User Information
    private String UploaderId;      // Uploader ID
    private String UploaderName;    // Uploader name
    private String UploaderAvatar;  // Uploader avatar

    // 6. Business Status (User-related)
    private boolean isLiked;        // Whether the current user has liked it
    private boolean isFavorited;    // Whether the current user has favorited it
    private long LastPlayPosition;  // Last playback position (milliseconds)
    private Date LastPlayTime;      // Last playback time

    // Constructor
    public VideoItem() {}

    // Reading video information
    protected VideoItem(Parcel in) {
        VideoId = in.readString();
        Title = in.readString();
        Description = in.readString();
        ShortDescription = in.readString();
        VideoPath = in.readString();
        Duration = in.readLong();
        FileSize = in.readLong();
        Format = in.readString();
        ResolutionHeight = in.readInt();
        ResolutionWidth = in.readInt();
        Category = in.readString();
        Tags = in.createStringArrayList();
        UploadTime = new Date(in.readLong());
        ThumbnailUrl = in.readString();
        ThumbnailPath = in.readString();
        PlayCount = in.readInt();
        LikeCount = in.readInt();
        FavoriteCount = in.readInt();
        CommentCount = in.readInt();
        UploaderId = in.readString();
        UploaderName = in.readString();
        UploaderAvatar = in.readString();
        isLiked = in.readByte() != 0;
        isFavorited = in.readByte() != 0;
        LastPlayPosition = in.readLong();
        LastPlayTime = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(VideoId);
        dest.writeString(Title);
        dest.writeString(Description);
        dest.writeString(ShortDescription);
        dest.writeString(VideoPath);
        dest.writeLong(Duration);
        dest.writeLong(FileSize);
        dest.writeString(Format);
        dest.writeInt(ResolutionWidth);
        dest.writeInt(ResolutionHeight);
        dest.writeString(Category);
        dest.writeStringList(Tags);
        dest.writeLong(UploadTime != null ? UploadTime.getTime() : -1);
        dest.writeString(ThumbnailUrl);
        dest.writeString(ThumbnailPath);
        dest.writeInt(PlayCount);
        dest.writeInt(LikeCount);
        dest.writeInt(FavoriteCount);
        dest.writeInt(CommentCount);
        dest.writeString(UploaderId);
        dest.writeString(UploaderName);
        dest.writeString(UploaderAvatar);
        dest.writeByte((byte) (isLiked ? 1 : 0));
        dest.writeByte((byte) (isFavorited ? 1 : 0));
        dest.writeLong(LastPlayPosition);
        dest.writeLong(LastPlayTime != null ? LastPlayTime.getTime() : -1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoItem> CREATOR = new Creator<VideoItem>() {
        @Override
        public VideoItem createFromParcel(Parcel in) {
            return new VideoItem(in);
        }

        @Override
        public VideoItem[] newArray(int size) {
            return new VideoItem[size];
        }
    };

    // All getter and setter methods, returning encapsulated data
    public String getVideoId() {
        return VideoId;
    }
    public void setVideoId(String videoId) {
        VideoId = videoId;
    }

    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }

    public String getVideoPath() {
        return VideoPath;
    }

    public String getDescription() {
        return Description;
    }
    public void setDescription(String description) {
        Description = description;
    }

    public long getDuration() {
        return Duration;
    }

    public long getFileSize() {
        return FileSize;
    }
    public String getFormattedFileSize() {
        return VideoUtils.formatFileSize(FileSize);
    }

    public Date getUploadTime() {
        return UploadTime;
    }

    public int getPlayCount() {
        return PlayCount;
    }
    public void setPlayCount(int playCount) {
        PlayCount = playCount;
    }

    public int getLikeCount() {
        return LikeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }
    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isFavorited() {
        return isFavorited;
    }
    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public long getLastPlayPosition() {
        return LastPlayPosition;
    }
    public void setLastPlayPosition(long lastPlayPosition) {
        LastPlayPosition = lastPlayPosition;
    }

    public String getCategory() {
        return Category;
    }
// Setter for testing purposes, no practical meaning

    public String getUploaderName() {
        return UploaderName;
    }

    public void setDuration(long duration) {
        Duration = duration;
    }

    public void setResolutionWidth(int resolutionWidth) {
        ResolutionWidth = resolutionWidth;
    }

    public void setResolutionHeight(int resolutionHeight) {
        ResolutionHeight = resolutionHeight;
    }
    public void setUploadTime(Date uploadTime) {
        UploadTime = uploadTime;
    }

    public void setVideoPath(String videoPath) {
        VideoPath = videoPath;
    }

    public void setFileSize(long fileSize) {
        FileSize = fileSize;
    }

    public void setFormat(String format) {
        Format = format;
    }

    public void setLikeCount(int likeCount) {
        LikeCount = likeCount;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public void setUploaderName(String uploaderName) {
        UploaderName = uploaderName;
    }

    public String getThumbnailUrl() {
        return ThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        ThumbnailUrl = thumbnailUrl;
    }

    public String getThumbnailPath() {
        return ThumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        ThumbnailPath = thumbnailPath;
    }

    // Using utility class to obtain formatted information
    public String getFormatDuration(){
        return VideoUtils.formatDuration(Duration);
    }
    public String getFormatPlayCount(){
        return VideoUtils.formatCount(PlayCount)+"次播放";
    }
    public String getFormatUploadTime(){
        return VideoUtils.formatUploadTime(UploadTime);
    }
    public String getFormatResolution(){
        return VideoUtils.formatResolution(ResolutionWidth,ResolutionHeight);
    }
    public String getFormatLikeCount(){
        return VideoUtils.formatCount(LikeCount)+"次赞";
    }

    public String getVideoInfoSummary() {
        return String.format("%s · %s · %s",
                getFormatPlayCount(),
                getFormatDuration(),
                getFormatUploadTime()
        );
    }


}
