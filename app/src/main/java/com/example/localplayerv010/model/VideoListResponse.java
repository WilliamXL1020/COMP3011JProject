package com.example.localplayerv010.model;

import java.util.List;

public class VideoListResponse {
    private int page;
    private int per_page;
    private int total_results;
    private String url;
    private List<PexelsVideo> videos;

    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }






    public int getPer_page() {
        return per_page;
    }
    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }




    public int getTotal_results() {
        return total_results;
    }
    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }




    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }




    public List<PexelsVideo> getVideos() {
        return videos;
    }
    public void setVideos(List<PexelsVideo> videos) {
        this.videos = videos;
    }
}
