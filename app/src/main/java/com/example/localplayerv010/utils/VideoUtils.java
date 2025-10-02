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
    //工具类一般都是静态方法不需要实例

    private VideoUtils(){
        throw new IllegalStateException("工具类不要实例化");
    }

    //毫秒转具体时间表现
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

    //秒转时间表现
    public static String fomatDurationSecond(int seconds){
        return formatDuration((long)seconds * 1000L);
    }


    //文字时间格式，毫秒版本
    public static String formatDurationDetailed(long durationMs) {
        if (durationMs <= 0) return "0分钟";

        long totalMinutes = durationMs / (1000 * 60);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        if (hours > 0) {
            return String.format("%d小时%d分钟", hours, minutes);
        } else {
            return String.format("%d分钟", minutes);
        }
    }

    //文字时间格式，秒版本
    public static String formatDurationDetailedFromSeconds(int totalSeconds) {
        if (totalSeconds <= 0) return "0秒";

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int secs = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%d小时%d分钟", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%d分钟%d秒", minutes, secs);
        } else {
            return String.format("%d秒", secs);
        }
    }






    //这里开始写针对文件大小的格式工具类
    public static String formatFileSize(long sizeBytes) {
        if (sizeBytes <= 0) return "0 B";

        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(sizeBytes) / Math.log10(1024));

        // 防止数组越界避免超过gb的存在
        digitGroups = Math.min(digitGroups, units.length - 1);

        return String.format("%.1f %s", sizeBytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }



    //数字处理工具类
    public static String formatCount(int count) {
        if (count < 0) return "0";
        if (count < 1000) return String.valueOf(count);
        if (count < 10000) return String.format("%.1f千", count / 1000.0);
        if (count < 100000000) return String.format("%.1f万", count / 10000.0);
        return String.format("%.1f亿", count / 100000000.0);
    }

    //时间处理工具类
    public static String formatUploadTime(Date uploadTime) {
        if (uploadTime == null) return "未知时间";

        long currentTime = System.currentTimeMillis();
        long uploadTimeMs = uploadTime.getTime();
        long diff = currentTime - uploadTimeMs;

        if (diff < 0) return "未来时间";

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = days / 30;
        long years = days / 365;

        if (seconds < 60) return "刚刚";
        if (minutes < 60) return minutes + "分钟前";
        if (hours < 24) return hours + "小时前";
        if (days == 1) return "昨天";
        if (days < 7) return days + "天前";
        if (weeks < 4) return weeks + "周前";
        if (months < 12) return months + "个月前";
        return years + "年前";
    }

    //分辨率格式化工具类
    public static String formatResolution(int width, int height) {
        if (width <= 0 || height <= 0) return "未知分辨率";
        return width + "×" + height;
    }
    //分辨率标签
    public static String getResolutionTag(int width, int height) {
        if (width <= 0 || height <= 0) return "未知";

        int longerSide = Math.max(width, height);

        if (longerSide >= 3840) return "4K";
        if (longerSide >= 2560) return "2K";
        if (longerSide >= 1920) return "1080P";
        if (longerSide >= 1280) return "720P";
        if (longerSide >= 854) return "480P";
        return "标清";
    }
    //分辨率信息
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
