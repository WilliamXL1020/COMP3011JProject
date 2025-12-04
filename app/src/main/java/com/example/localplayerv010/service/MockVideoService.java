package com.example.localplayerv010.service;

import com.example.localplayerv010.model.VideoItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockVideoService {
    public static List<VideoItem> getHomeVideo(){
        List<VideoItem> videos = new ArrayList<>();

        String[] testVideoUrls = {
            "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",
            "https://media.w3.org/2010/05/sintel/trailer.mp4",
            "https://vjs.zencdn.net/v/oceans.mp4"
        };
        String[] categories = {"gaming", "music", "lifestyle", "education", "technology", "movie", "funny"};
        String[] uploaders = {"John", "Tech expert", "Lifestyle blogger", "gaming expert", "Music lovers"};

        // 创建40个模拟视频
        for (int i = 1; i <= 40; i++) {
            VideoItem video = new VideoItem();
            video.setVideoId("video_" + i);
            video.setTitle("Amazing Videos " + i);
            video.setDescription("This is a detailed description of the " + i + "th test video, containing various interesting content.");
            video.setVideoPath(testVideoUrls[i % testVideoUrls.length]); // Recycle test URL
            video.setDuration(60000 * (i % 10 + 1)); // 1-10min
            video.setFileSize(10485760L * (i % 5 + 1)); // 10-50MB
            video.setPlayCount(1000 + i * 50);
            video.setLikeCount(100 + i * 10);
            video.setCategory(categories[i % categories.length]);
            video.setUploaderName(uploaders[i % uploaders.length]);
            video.setUploadTime(new Date(System.currentTimeMillis() - i * 24 * 60 * 60 * 1000L)); // 1-20 days before

            videos.add(video);
        }
        return videos;
    }
}
