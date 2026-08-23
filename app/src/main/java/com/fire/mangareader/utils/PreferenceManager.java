package com.fire.mangareader.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "manga_fire_prefs";
    private static final String KEY_THEME = "theme";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_WIFI_ONLY = "wifi_only";
    private static final String KEY_READING_DIRECTION = "reading_direction";
    private static final String KEY_USER_UID = "user_uid";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_IS_GUEST = "is_guest";
    private static final String KEY_FIRST_LAUNCH = "first_launch";

    private final SharedPreferences prefs;

    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Theme: "dark", "light", "system"
    public String getTheme() { return prefs.getString(KEY_THEME, "dark"); }
    public void setTheme(String theme) { prefs.edit().putString(KEY_THEME, theme).apply(); }
    
        // Bio
    public String getUserBio() { return prefs.getString("user_bio", "قارئ شغوف بالمانها والمانجا ⚡"); }
    public void setUserBio(String bio) { prefs.edit().putString("user_bio", bio).apply(); }

    // Profile Picture URI
    public String getProfilePic() { return prefs.getString("profile_pic", null); }
    public void setProfilePic(String uri) { prefs.edit().putString("profile_pic", uri).apply(); }

    // Language: "en", "ar"
    public String getLanguage() { return prefs.getString(KEY_LANGUAGE, "ar"); }
    public void setLanguage(String lang) { prefs.edit().putString(KEY_LANGUAGE, lang).apply(); }
    
        // دالة لحفظ مسار صورة الغلاف
    public void setProfileBanner(String imageUri) {
        android.content.SharedPreferences.Editor editor = prefs.edit();
        editor.putString("profile_banner", imageUri);
        editor.apply();
    }

    // دالة لاسترجاع مسار صورة الغلاف
    public String getProfileBanner() {
        return prefs.getString("profile_banner", null);
    }

    // WiFi only
    public boolean isWifiOnly() { return prefs.getBoolean(KEY_WIFI_ONLY, true); }
    public void setWifiOnly(boolean wifiOnly) { prefs.edit().putBoolean(KEY_WIFI_ONLY, wifiOnly).apply(); }

    // Reading direction: "ltr", "rtl"
    public String getReadingDirection() { return prefs.getString(KEY_READING_DIRECTION, "ltr"); }
    public void setReadingDirection(String dir) { prefs.edit().putString(KEY_READING_DIRECTION, dir).apply(); }

    // User
    public void saveUser(String uid, String email, String name, boolean isGuest) {
        prefs.edit()
                .putString(KEY_USER_UID, uid)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_NAME, name)
                .putBoolean(KEY_IS_GUEST, isGuest)
                .apply();
    }

    public void clearUser() {
        prefs.edit()
                .remove(KEY_USER_UID)
                .remove(KEY_USER_EMAIL)
                .remove(KEY_USER_NAME)
                .remove(KEY_IS_GUEST)
                .apply();
    }

    public String getUserUid() { return prefs.getString(KEY_USER_UID, null); }
    public String getUserEmail() { return prefs.getString(KEY_USER_EMAIL, null); }
    public String getUserName() { return prefs.getString(KEY_USER_NAME, null); }
    public void setUserName(String name) { prefs.edit().putString(KEY_USER_NAME, name).apply(); }
    public boolean isGuest() { return prefs.getBoolean(KEY_IS_GUEST, true); }
    public boolean isLoggedIn() { return getUserUid() != null; }

    public boolean isFirstLaunch() {
        boolean first = prefs.getBoolean(KEY_FIRST_LAUNCH, true);
        if (first) prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
        return first;
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
