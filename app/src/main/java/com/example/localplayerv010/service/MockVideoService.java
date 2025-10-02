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
        String[] categories = {"游戏", "音乐", "生活", "知识", "科技"};
        String[] uploaders = {"小明同学", "技术达人", "生活博主", "游戏高手", "音乐爱好者"};

        // 创建20个模拟视频
        for (int i = 1; i <= 20; i++) {
            VideoItem video = new VideoItem();
            video.setVideoId("video_" + i);
            video.setTitle("精彩视频 " + i);
            video.setDescription("这是第" + i + "个测试视频的详细描述信息，包含各种有趣的内容。");
            video.setVideoPath(testVideoUrls[i % testVideoUrls.length]); // 循环使用测试URL
            video.setDuration(60000 * (i % 10 + 1)); // 1-10分钟
            video.setFileSize(10485760L * (i % 5 + 1)); // 10-50MB
            video.setPlayCount(1000 + i * 50);
            video.setLikeCount(100 + i * 10);
            video.setCategory(categories[i % categories.length]);
            video.setUploaderName(uploaders[i % uploaders.length]);
            video.setUploadTime(new Date(System.currentTimeMillis() - i * 24 * 60 * 60 * 1000L)); // 1-20天前

            videos.add(video);
        }
        return videos;
    }
}
