package com.fire.mangareader.domain.model;

import java.util.HashMap;
import java.util.Map;

public class Comment {
    public String id;
    public String comment_id;
    public String mangaUrl;
    public String user_id;
    public String user_name;
    public String username;
    public String user_avatar;
    public String text;
    public long timestamp;
    public String created_at;
    public boolean isSpoiler;
    public boolean is_spoiler;
    public int likes;
    public int dislikes;
    public Map<String, Boolean> liked_by = new HashMap<>();
    public Map<String, Boolean> disliked_by = new HashMap<>();
    public String parent_id;
    public int replies_count;
    public Map<String, Comment> replies = new HashMap<>();

    public Comment() {}

    public Comment(String mangaUrl, String username, String text, long timestamp, boolean isSpoiler) {
        this.mangaUrl = mangaUrl;
        this.username = username;
        this.user_name = username;
        this.text = text;
        this.timestamp = timestamp;
        this.isSpoiler = isSpoiler;
        this.is_spoiler = isSpoiler;
    }
}

