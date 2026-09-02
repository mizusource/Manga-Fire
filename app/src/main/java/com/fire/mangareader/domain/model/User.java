package com.fire.mangareader.domain.model;

public class User {
    private String uid;
    private String email;
    private String displayName;
    private String photoUrl;
    private boolean isGuest;

    public User() {}

    public User(String uid, String email, String displayName, String photoUrl, boolean isGuest) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.isGuest = isGuest;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public boolean isGuest() { return isGuest; }
    public void setGuest(boolean guest) { isGuest = guest; }
}
