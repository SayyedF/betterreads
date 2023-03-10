package com.jilani.search;

import java.util.List;

public class SearchResultBook {
    private String key;
    private String title;
    private List<String> author_name;
    private List<String> author_key;
    private String cover_i;

    public SearchResultBook() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(List<String> author_name) {
        this.author_name = author_name;
    }

    public List<String> getAuthor_key() {
        return author_key;
    }

    public void setAuthor_key(List<String> author_key) {
        this.author_key = author_key;
    }

    public String getCover_i() {
        return cover_i;
    }

    public void setCover_i(String cover_i) {
        this.cover_i = cover_i;
    }
}
