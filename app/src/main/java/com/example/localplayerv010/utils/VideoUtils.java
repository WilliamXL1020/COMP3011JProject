package com.example.localplayerv010.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.localplayerv010.model.VideoItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VideoUtils {
    // Utility classes are generally static methods and do not require instances

    private VideoUtils(){
        throw new IllegalStateException("Do not instantiate utility classes");
    }

    // Milliseconds to specific time representation
    public static String formatDuration(long durationMs){
        if (durationMs <= 0) return "00:00";

        long totalSeconds = durationMs/1000;
        long hours = totalSeconds/3600;
        long minutes = (totalSeconds%3600)/60;
        long second = totalSeconds%60;
        if (hours>0){
            return String.format("%02d:%02d:%02d",hours,minutes,second);
        }else {
            return String.format("%02d:%02d",minutes,second);
        }
    }

    // Refresh rate performance
    public static String fomatDurationSecond(int seconds){
        return formatDuration((long)seconds * 1000L);
    }


    // Text time format, millisecond version
    public static String formatDurationDetailed(long durationMs) {
        if (durationMs <= 0) return "0min";

        long totalMinutes = durationMs / (1000 * 60);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        if (hours > 0) {
            return String.format("%hurs%dmins", hours, minutes);
        } else {
            return String.format("%dmins", minutes);
        }
    }

    //Text time format, seconds version
    public static String formatDurationDetailedFromSeconds(int totalSeconds) {
        if (totalSeconds <= 0) return "0sec";

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int secs = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%d hur%d min", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%d mins%d secs", minutes, secs);
        } else {
            return String.format("%d secs", secs);
        }
    }






    // Here we begin writing a formatting utility class for file sizes
    public static String formatFileSize(long sizeBytes) {
        if (sizeBytes <= 0) return "0 B";

        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(sizeBytes) / Math.log10(1024));

        // Prevent array out-of-bounds access and avoid arrays exceeding GB
        digitGroups = Math.min(digitGroups, units.length - 1);

        return String.format("%.1f %s", sizeBytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }



    //Digital processing tools
    public static String formatCount(int count) {
        if (count < 0) return "0";
        if (count < 1000) return String.valueOf(count);
        if (count < 10000) return String.format("%.1f千", count / 1000.0);
        if (count < 100000000) return String.format("%.1f万", count / 10000.0);
        return String.format("%.1f亿", count / 100000000.0);
    }

    //Time processing utility class
    public static String formatUploadTime(Date uploadTime) {
        if (uploadTime == null) return "unknown time";

        long currentTime = System.currentTimeMillis();
        long uploadTimeMs = uploadTime.getTime();
        long diff = currentTime - uploadTimeMs;

        if (diff < 0) return "future time";

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = days / 30;
        long years = days / 365;

        if (seconds < 60) return "just now";
        if (minutes < 60) return minutes + "minutes ago";
        if (hours < 24) return hours + "hours ago";
        if (days == 1) return "yesterday";
        if (days < 7) return days + "days ago";
        if (weeks < 4) return weeks + "weeks ago";
        if (months < 12) return months + "months ago";
        return years + "years ago";
    }

    // Resolution formatting utility class
    public static String formatResolution(int width, int height) {
        if (width <= 0 || height <= 0) return "Unknown resolution";
        return width + "×" + height;
    }
    // Resolution label
    public static String getResolutionTag(int width, int height) {
        if (width <= 0 || height <= 0) return "unknown";

        int longerSide = Math.max(width, height);

        if (longerSide >= 3840) return "4K";
        if (longerSide >= 2560) return "2K";
        if (longerSide >= 1920) return "1080P";
        if (longerSide >= 1280) return "720P";
        if (longerSide >= 854) return "480P";
        return "Standard";
    }
    // Resolution information
    public static String getFullResolutionInfo(int width, int height) {
        return formatResolution(width, height) + " (" + getResolutionTag(width, height) + ")";
    }

//    扫描对应路径下本地文件
//    public static List<VideoItem> scanRealVideos(Context context) {
//        List<VideoItem> videoList = new ArrayList<>();
//
//        try {
//            // 只扫描两个最常见目录
//            String[] scanPaths = {
////                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),
////                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera"
//                    "/sdcard/Pictures",
//                    context.getExternalFilesDir(null).getAbsolutePath()
//            };
//
//            for (String path : scanPaths) {
//                File directory = new File(path);
//                if (directory.exists()) {
//                    File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4"));
//
//                    if (files != null) {
//                        for (File file : files) {
//                            Log.d("VideoScan", "找到视频: " + file.getName() + ", 大小: " + file.length());
//
//                            // 创建VideoItem并填充真实文件信息
//                            VideoItem video = new VideoItem();
//                            video.setVideoId("real_" + System.currentTimeMillis()); // 简单ID
//                            video.setTitle(file.getName());
//                            video.setVideoPath("file://" + file.getAbsolutePath()); // 注意加file://
//                            video.setFileSize(file.length());
//                            video.setUploadTime(new Date(file.lastModified()));
//                            video.setFormat("mp4");
//
//                            videoList.add(video);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Toast.makeText(context, "扫描出错: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            Log.e("VideoScan", "扫描失败: " + e.getMessage());
//        }
//
//        return videoList;
//    }
}
