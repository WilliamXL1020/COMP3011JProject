package com.example.localplayerv010.model;

import java.util.List;

public class PexelsVideo {
    private int id;
    private int width;
    private int height;
    private String url;
    private String image;
    private Object full_res; // 可能是null，用Object
    private List<String> tags;
    private int duration;
    private UserOnline user;
    private List<VideoFile> video_files;
    private List<VideoPicture> video_pictures;






    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }





    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }



    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }



    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }



    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }



    public Object getFull_res() {
        return full_res;
    }
    public void setFull_res(Object full_res) {
        this.full_res = full_res;
    }



    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }




    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }



    public UserOnline getUser() {
        return user;
    }
    public void setUser(UserOnline user) {
        this.user = user;
    }



    public List<VideoFile> getVideo_files() {
        return video_files;
    }
    public void setVideoFiles(List<VideoFile> video_files) {
        this.video_files = video_files;
    }


    public List<VideoPicture> getVideo_pictures() {
        return video_pictures;
    }
    public void setVideo_pictures(List<VideoPicture> video_pictures) {
        this.video_pictures = video_pictures;
    }


    public String getBestQualityVideoUrl() {
        if (video_files == null || video_files.isEmpty()) return null;

        // 优先返回hd质量的视频
        for (VideoFile file : video_files) {
            if ("hd".equals(file.getQuality())) {
                return file.getLink();
            }
        }

        // 如果没有hd，返回第一个
        return video_files.get(0).getLink();
    }
}
