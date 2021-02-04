package com.example.envdataproject.home;

import java.util.ArrayList;

public class HomeItem {
    private String time;
    private ArrayList<String> tag;
    private String author;
    private Integer idx;

    public HomeItem(String time, ArrayList<String> tag, String author,Integer idx) {
        this.time = time;
        this.tag = tag;
        this.author = author;
        this.idx = idx;
    }

    public String getTime() {
        return time;
    }

    public String getAuthor() {
        return author;
    }

    public ArrayList<String> getTag() {
        return tag;
    }

    public Integer getIdx() {
        return idx;
    }
}
