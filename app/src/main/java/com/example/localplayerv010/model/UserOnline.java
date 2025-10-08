package com.example.localplayerv010.model;

public class UserOnline {
    private int id;
    private String name;
    private String url;

    public UserOnline(){}

    public int getID() {
        return id;
    }
    public void setID(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        name = name;
    }


    public String getURL() {
        return url;
    }
    public void setURL(String url) {
        this.url = url;
    }
}
