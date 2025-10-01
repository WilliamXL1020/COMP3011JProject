package com.example.localplayerv010.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

public class VideoItem implements Parcelable {

    // 1. 基础标识信息
    private String VideoId;          // 视频唯一ID
    private String Title;           // 视频标题
    private String Description;     // 视频描述
    private String ShortDescription;// 简短描述（用于列表显示）

    // 2. 媒体文件信息
    private String VideoPath;       // 本地文件路径 或 网络URL
    private long Duration;          // 视频时长（毫秒）
    private long FileSize;          // 文件大小（字节）
    private String Format;          // 视频格式：mp4, mkv等
    private int ResolutionWidth;    // 分辨率宽
    private int ResolutionHeight;   // 分辨率高

    // 3. 元数据信息
    private String Category;        // 分类：游戏、音乐、影视等
    private List<String> Tags;      // 标签列表
    private Date UploadTime;        // 上传时间
    private String ThumbnailUrl;    // 缩略图网络URL
    private String ThumbnailPath;   // 缩略图本地路径

    // 4. 统计信息
    private int PlayCount;          // 播放次数
    private int LikeCount;          // 点赞数
    private int FavoriteCount;      // 收藏数
    private int CommentCount;       // 评论数

    // 5. 用户信息
    private String UploaderId;      // 上传者ID
    private String UploaderName;    // 上传者名称
    private String UploaderAvatar;  // 上传者头像

    // 6. 业务状态（用户相关）
    private boolean isLiked;        // 当前用户是否点赞
    private boolean isFavorited;    // 当前用户是否收藏
    private long LastPlayPosition;  // 最后播放位置（毫秒）
    private Date LastPlayTime;      // 最后播放时间


    //构造函数
    public VideoItem() {}
    public VideoItem(String videoId, String title, String videoPath) {
        this.VideoId = VideoId;
        this.Title = Title;
        this.VideoPath = VideoPath;
    }

    //读取视频信息
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

    //所有getter方法以及setter方法，针对封装后的数据做返回
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
//    public String getFormattedFileSize() {
//        return VideoUtils.formatFileSize(FileSize);
//    }

    public Date getUploadTime() {
        return UploadTime;
    }


    public int getPlayCount() {
        return PlayCount;
    }
    public void setPlayCount(int playCount) {
        PlayCount = playCount;
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


}
