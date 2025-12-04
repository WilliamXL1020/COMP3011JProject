//Convert JSON data to videoitem information

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

        // ID conversion: int → String
        videoItem.setVideoId(String.valueOf(pexelsVideo.getId()));

        // Title: Pexels does not have a title field; it needs to be created
        String title = createTitle(pexelsVideo);
        videoItem.setTitle(title);

        // description
        videoItem.setDescription("High-quality video content from Pexels");

        // Video path: Select the video link with the best quality
        videoItem.setVideoPath(getBestVideoUrl(pexelsVideo));

        // Duration conversion: seconds → milliseconds
        videoItem.setDuration(pexelsVideo.getDuration() * 1000L);

        // Uploader
        if (pexelsVideo.getUser() != null) {
            videoItem.setUploaderName(pexelsVideo.getUser().getName());
        } else {
            videoItem.setUploaderName("Pexels users");
        }



        if (pexelsVideo.getImage() != null) {
            videoItem.setThumbnailUrl(pexelsVideo.getImage());
            Log.d("VideoConverter", "✅ Set video cover: " + pexelsVideo.getImage());
        } else if (pexelsVideo.getVideo_pictures() != null &&
                !pexelsVideo.getVideo_pictures().isEmpty()) {
            // If there is no main thumbnail, use the first preview image
            String firstPreview = pexelsVideo.getVideo_pictures().get(0).getPicture();
            videoItem.setThumbnailUrl(firstPreview);
            Log.d("VideoConverter", "✅ Use the preview image as the cover: " + firstPreview);
        } else {
            Log.w("VideoConverter", "⚠️ No available cover image found");
        }

        // Set default business data (because the Pexels API does not provide this)
        videoItem.setPlayCount(1000 + (int)(Math.random() * 1000)); // Random play count
        videoItem.setLikeCount(50 + (int)(Math.random() * 100));   // Random number of likes
        videoItem.setCategory("selected"); // Default Category
        videoItem.setFileSize(10485760L); // Default file size 10MB

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
        Log.d("VideoConverter", "=== Starting to get video URL, ID: " + pexelsVideo.getId() + " ===");

        // Check if video_files exists
        if (pexelsVideo.getVideo_files() == null) {
            Log.e("VideoConverter", "❌ video_files is null");
            return "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        }

        if (pexelsVideo.getVideo_files().isEmpty()) {
            Log.e("VideoConverter", "❌ video_files is Empty array");
            return "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        }

        Log.d("VideoConverter", "✅ video_files amounts: " + pexelsVideo.getVideo_files().size());

        // Check the basic information of the first file
        VideoFile firstFile = pexelsVideo.getVideo_files().get(0);
        Log.d("VideoConverter", "first file - quality: " + firstFile.getQuality() +
                ", file_type: " + firstFile.getFileType() +
                ", link: " + firstFile.getLink());

        // The original selection logic...
        for (VideoFile file : pexelsVideo.getVideo_files()) {
            if ("hd".equals(file.getQuality()) && "video/mp4".equals(file.getFileType())) {
                Log.d("VideoConverter", "✅ Find HD MP4 videos: " + file.getLink());
                return file.getLink();
            }
        }

        // If hd is not available, return the first mp4 file
        for (VideoFile file : pexelsVideo.getVideo_files()) {
            if ("video/mp4".equals(file.getFileType())) {
                Log.d("VideoConverter", "✅ Find SD MP4 videos: " + file.getLink());
                return file.getLink();
            }
        }

        // If not yet, return to the first file
        String firstUrl = pexelsVideo.getVideo_files().get(0).getLink();
        return firstUrl;
    }

    private static String createTitle(PexelsVideo pexelsVideo) {
        Log.d("VideoConverter", "create topic，user: " + (pexelsVideo.getUser() != null ? pexelsVideo.getUser().getName() : "null"));

        if (pexelsVideo.getUser() != null && pexelsVideo.getUser().getName() != null) {
            return pexelsVideo.getUser().getName() + "'s magnificent videos";
        } else {
            // 如果user为null，使用其他信息创建标题
            return "magnificent video " + pexelsVideo.getId();
        }
    }
}