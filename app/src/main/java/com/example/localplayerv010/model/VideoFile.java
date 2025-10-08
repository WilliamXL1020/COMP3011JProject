package com.example.localplayerv010.model;

public class VideoFile {
    private int id;
    private String quality;
    private String FileType;
    private int width;
    private int height;
    private double fps;
    private String link;

    public VideoFile(){}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }



    public String getQuality() {
        return quality;
    }
    public void setQuality(String quality) {
        this.quality = quality;
    }


    public String getFileType() {
        return FileType;
    }
    public void setFile_type(String file_type) {
        this.FileType = file_type;
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



    public double getFps() {
        return fps;
    }
    public void setFps(double fps) {
        this.fps = fps;
    }



    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
}
