package com.fci.yehiahd.moviesapplication;

/**
 * Created by yehia on 26/03/16.
 */
public class ReviewData {
    private String author;
    private String content;
    private String url;

    public ReviewData(){
        this.author="";
        this.content="";
        this.url="";
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
