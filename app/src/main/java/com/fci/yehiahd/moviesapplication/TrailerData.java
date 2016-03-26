package com.fci.yehiahd.moviesapplication;

/**
 * Created by yehia on 25/03/16.
 */
public class TrailerData {

    private String id;
    private String key;
    private String name;
    private String site;
    private int size;
    private String type;

    public TrailerData(){
        this.id="";
        this.key="";
        this.name="";
        this.site="";
        this.size=0;
        this.type="";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
