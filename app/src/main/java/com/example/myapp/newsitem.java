package com.example.myapp;


import android.view.View;

public class newsitem {
    private String source;
    private String headline;
    private String summary;
    private String url;
    private String image;
    private int timeStamp;




    public newsitem(String source,String headline,String summary,String url,String image,int timeStamp) {

        this.source = source;
        this.headline = headline;
        this.summary = summary;
        this.url = url;
        this.image = image;
        this.timeStamp = timeStamp;

    }

    public String getSource() {
        return source;
    }
    public String getHeadline() {
        return headline;
    }
    public String getSummary() {
        return summary;
    }
    public String getUrl() {
        return url;
    }
    public int getTimeStamp() {
        return timeStamp;
    }
    public String getImage(){
        return image;
    }





}

