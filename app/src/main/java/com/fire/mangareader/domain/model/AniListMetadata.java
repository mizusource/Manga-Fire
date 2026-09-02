package com.fire.mangareader.domain.model;

public class AniListMetadata {
    public int anilistId;
    public String author = "";
    public String artist = "";
    public String format = "MANGA";
    public String sourceFormat = "MANGA";
    public String countryOfOrigin = "JP";
    public String originCountry = "JP";
    public String startDateText = "";
    public String endDateText = "";
    public String statusArabic = "";
    public String scoreText = "";
    public String synopsisArabic = "";
    public String bannerUrl = "";
    public String coverUrl = "";
    public int averageScore = 0;
    public int popularity = 0;
    public java.util.List<String> genres = new java.util.ArrayList<>();
    public long lastUpdated = System.currentTimeMillis();

    public AniListMetadata() {}

    public String getFormattedDates() {
        if (!startDateText.isEmpty() && !endDateText.isEmpty()) {
            return startDateText + " - " + endDateText;
        } else if (!startDateText.isEmpty()) {
            return startDateText;
        }
        return "غير محدد";
    }
}
