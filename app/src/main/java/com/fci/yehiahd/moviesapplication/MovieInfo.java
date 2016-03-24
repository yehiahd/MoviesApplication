package com.fci.yehiahd.moviesapplication;

/**
 * Created by yehia on 23/03/16.
 */
public class MovieInfo {
    private String poster_path;
    private String overview;
    private String release_date;
    private String id;
    private String original_title;
    private String title;
    private String backdrop_path;
    private double popularity;
    private int vote_count;
    private double vote_average;

    MovieInfo(){
        this.poster_path="";
        this.overview = "";
        this.release_date = "";
        this.id="";
        this.original_title="";
        this.title="";
        this.backdrop_path="";
        this.popularity=0.0;
        this.vote_count=0;
        this.vote_average=0.0;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }
}
