package com.fire.mangareader.model;

public class Manga {
    private String title;
    private String url;
    private String coverUrl;
    private String latestChapter;
    private String rating;

    public Manga() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getLatestChapter() { return latestChapter; }
    public void setLatestChapter(String latestChapter) { this.latestChapter = latestChapter; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
}
