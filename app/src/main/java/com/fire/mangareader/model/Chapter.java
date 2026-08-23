package com.fire.mangareader.model;

public class Chapter {
    private String url;
    private String title;
    private String chapterNumber;

    public Chapter() {}

    public Chapter(String url, String title, String chapterNumber) {
        this.url = url;
        this.title = title;
        this.chapterNumber = chapterNumber;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(String chapterNumber) { this.chapterNumber = chapterNumber; }
    
    public String getId() { return url; }
}
