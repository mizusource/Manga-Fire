package com.fire.mangareader.util;

public class GlobalMangaStats {
    public double overallAverage = 8.8;
    public double storyAverage = 8.5;
    public double charactersAverage = 9.0;
    public double artAverage = 9.2;
    public int totalVotes = 120;

    public GlobalMangaStats() {}

    public GlobalMangaStats(double overallAverage, double storyAverage, double charactersAverage, double artAverage, int totalVotes) {
        this.overallAverage = overallAverage;
        this.storyAverage = storyAverage;
        this.charactersAverage = charactersAverage;
        this.artAverage = artAverage;
        this.totalVotes = totalVotes;
    }
}
