//json获取数据信息转换为videoitem信息

package com.example.localplayerv010.utils;


import android.util.Log;

import com.example.localplayerv010.model.PexelsVideo;
import com.example.localplayerv010.model.VideoFile;
import com.example.localplayerv010.model.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class VideoConverter {


    public static VideoItem fromPexelsVideo(PexelsVideo pexelsVideo) {
        if (pexelsVideo == null) {
            return null;
        }

        VideoItem videoItem = new VideoItem();

        // ID转换：int → String
        videoItem.setVideoId(String.valueOf(pexelsVideo.getId()));

        // 标题：Pexels没有title字段，需要创建
        String title = createTitle(pexelsVideo);
        videoItem.setTitle(title);

        // 描述
        videoItem.setDescription("来自Pexels的优质视频内容");

        // 视频路径：选择最佳质量的视频链接
        videoItem.setVideoPath(getBestVideoUrl(pexelsVideo));

        // 时长转换：秒 → 毫秒
        videoItem.setDuration(pexelsVideo.getDuration() * 1000L);

        // 上传者
        if (pexelsVideo.getUser() != null) {
            videoItem.setUploaderName(pexelsVideo.getUser().getName());
        } else {
            videoItem.setUploaderName("Pexels用户");
        }



        if (pexelsVideo.getImage() != null) {
            videoItem.setThumbnailUrl(pexelsVideo.getImage());
            Log.d("VideoConverter", "✅ 设置视频封面: " + pexelsVideo.getImage());
        } else if (pexelsVideo.getVideo_pictures() != null &&
                !pexelsVideo.getVideo_pictures().isEmpty()) {
            // 如果没有主缩略图，使用第一张预览图
            String firstPreview = pexelsVideo.getVideo_pictures().get(0).getPicture();
            videoItem.setThumbnailUrl(firstPreview);
            Log.d("VideoConverter", "✅ 使用预览图作为封面: " + firstPreview);
        } else {
            Log.w("VideoConverter", "⚠️ 没有找到可用的封面图片");
        }

        // 设置默认的业务数据（因为Pexels API不提供这些）
        videoItem.setPlayCount(1000 + (int)(Math.random() * 1000)); // 随机播放量
        videoItem.setLikeCount(50 + (int)(Math.random() * 100));   // 随机点赞数
        videoItem.setCategory("精选"); // 默认分类
        videoItem.setFileSize(10485760L); // 默认文件大小10MB

        return videoItem;
    }

    public static List<VideoItem> fromPexelsVideos(List<PexelsVideo> pexelsVideos) {
        List<VideoItem> result = new ArrayList<>();
        if (pexelsVideos == null) {
            return result;
        }

        for (PexelsVideo pexelsVideo : pexelsVideos) {
            VideoItem videoItem = fromPexelsVideo(pexelsVideo);
            if (videoItem != null) {
                result.add(videoItem);
            }
        }
        return result;
    }


    private static String getBestVideoUrl(PexelsVideo pexelsVideo) {
        Log.d("VideoConverter", "=== 开始获取视频URL，ID: " + pexelsVideo.getId() + " ===");

        // 检查video_files是否存在
        if (pexelsVideo.getVideo_files() == null) {
            Log.e("VideoConverter", "❌ video_files为null");
            return "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        }

        if (pexelsVideo.getVideo_files().isEmpty()) {
            Log.e("VideoConverter", "❌ video_files为空数组");
            return "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        }

        Log.d("VideoConverter", "✅ video_files数量: " + pexelsVideo.getVideo_files().size());

        // 检查第一个文件的基本信息
        VideoFile firstFile = pexelsVideo.getVideo_files().get(0);
        Log.d("VideoConverter", "第一个文件 - quality: " + firstFile.getQuality() +
                ", file_type: " + firstFile.getFileType() +
                ", link: " + firstFile.getLink());

        // 原来的选择逻辑...
        for (VideoFile file : pexelsVideo.getVideo_files()) {
            if ("hd".equals(file.getQuality()) && "video/mp4".equals(file.getFileType())) {
                Log.d("VideoConverter", "✅ 找到HD MP4视频: " + file.getLink());
                return file.getLink();
            }
        }

        // 如果没有hd，返回第一个mp4文件
        for (VideoFile file : pexelsVideo.getVideo_files()) {
            if ("video/mp4".equals(file.getFileType())) {
                Log.d("VideoConverter", "✅ 找到SD MP4视频: " + file.getLink());
                return file.getLink();
            }
        }

        // 如果还没有，返回第一个文件
        String firstUrl = pexelsVideo.getVideo_files().get(0).getLink();
        return firstUrl;
    }

    private static String createTitle(PexelsVideo pexelsVideo) {
        Log.d("VideoConverter", "创建标题，user: " + (pexelsVideo.getUser() != null ? pexelsVideo.getUser().getName() : "null"));

        if (pexelsVideo.getUser() != null && pexelsVideo.getUser().getName() != null) {
            return pexelsVideo.getUser().getName() + "的精彩视频";
        } else {
            // 如果user为null，使用其他信息创建标题
            return "精彩视频 " + pexelsVideo.getId();
        }
    }
}