package com.fire.mangareader.domain.model;

import java.util.List;

public class Manga {
    private String id;
    private String title;
    private String url;
    private String coverUrl;
    private String latestChapter;
    private String rating;
    
    // New Advanced Fields
    private List<String> genres;
    private String storyStatus;
    private String type;
    private Double chapterNumber;
    private String description;

    public Manga() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public String getStoryStatus() { return storyStatus; }
    public void setStoryStatus(String storyStatus) { this.storyStatus = storyStatus; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(Double chapterNumber) { this.chapterNumber = chapterNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
