package com.fire.mangareader.model;

public class Comment {
    public String id;
    public String mangaUrl; // لربط التعليق بالمانهوا الصحيحة
    public String username;
    public String text;
    public long timestamp;
    public boolean isSpoiler; // هل يحتوي على حرق؟

    // Firebase يحتاج دائماً إلى مُنشئ فارغ (Empty Constructor)
    public Comment() {}

    public Comment(String mangaUrl, String username, String text, long timestamp, boolean isSpoiler) {
        this.mangaUrl = mangaUrl;
        this.username = username;
        this.text = text;
        this.timestamp = timestamp;
        this.isSpoiler = isSpoiler;
    }
}
