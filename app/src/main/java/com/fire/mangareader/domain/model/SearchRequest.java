package com.fire.mangareader.domain.model;

import java.util.List;

public class SearchRequest {
    private String query;
    private Integer status; // e.g. 0: All, 1: Ongoing, 2: Completed
    private Integer format; // e.g. Manga, Manhwa, Manhua
    private Integer year;
    private List<String> genres;
    
    public SearchRequest(String query) {
        this.query = query;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getFormat() { return format; }
    public void setFormat(Integer format) { this.format = format; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }
}
