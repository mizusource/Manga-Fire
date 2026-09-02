package com.fire.mangareader.domain.model.remote;

public class UserNotifications {
    private String id;
    private String userId;
    private String fromUserId;
    private String fromUserName;
    private String commentId;
    private String replyId;
    private String type; // e.g. "chapter_update", "comment_reply"
    private Boolean isRead;
    private String mangaId;
    private String mangaTitle;
    private long timestamp;

    public UserNotifications() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getFromUserId() { return fromUserId; }
    public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }
    public String getFromUserName() { return fromUserName; }
    public void setFromUserName(String fromUserName) { this.fromUserName = fromUserName; }
    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }
    public String getReplyId() { return replyId; }
    public void setReplyId(String replyId) { this.replyId = replyId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Boolean isRead() { return isRead; }
    public void setRead(Boolean read) { isRead = read; }
    public String getMangaId() { return mangaId; }
    public void setMangaId(String mangaId) { this.mangaId = mangaId; }
    public String getMangaTitle() { return mangaTitle; }
    public void setMangaTitle(String mangaTitle) { this.mangaTitle = mangaTitle; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
