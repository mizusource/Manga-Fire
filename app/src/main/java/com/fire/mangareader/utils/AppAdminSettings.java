package com.fire.mangareader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppAdminSettings {
    private static final String TAG = "AppAdminSettings";
    private static final String PREFS_NAME = "speed_manga_admin_settings";
    private static final String FIREBASE_PATH = "settings";

    private static boolean isInitialized = false;
    private static SharedPreferences prefs;

    public static boolean commentsEnabled = true;
    public static boolean repliesEnabled = true;
    public static boolean spoilersEnabled = true;
    public static boolean profanityFilterEnabled = true;
    public static List<String> bannedWords = new ArrayList<>(Arrays.asList("سبام", "مسيء", "شتم", "قذر", "كلب", "حمار"));
    public static boolean announcementEnabled = false;
    public static String announcementText = "";
    public static String announcementType = "info";
    public static String announcementLink = "";
    public static boolean maintenanceMode = false;
    public static String maintenanceMessage = "التطبيق يخضع للصيانة الدورية لتحسين الخدمة، سنعود قريباً!";
    public static boolean appUpdateAvailable = false;
    public static String latestVersionName = "1.0.0";
    public static String appUpdateUrl = "";

    public interface SettingsUpdateListener {
        void onSettingsUpdated();
    }

    private static final List<SettingsUpdateListener> listeners = new ArrayList<>();

    public static void addListener(SettingsUpdateListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void removeListener(SettingsUpdateListener listener) {
        listeners.remove(listener);
    }

    public static void initialize(Context context) {
        if (isInitialized) return;
        isInitialized = true;
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadFromLocal();
    }
    public static String filterProfanity(String text) {
        if (text == null) return "";
        if (!profanityFilterEnabled) return text;
        String result = text;
        for (String word : bannedWords) {
            if (word != null && !word.trim().isEmpty()) {
                result = result.replaceAll("(?i)" + java.util.regex.Pattern.quote(word), "***");
            }
        }
        return result;
    }

    private static void loadFromLocal() {
        if (prefs == null) return;
        commentsEnabled = prefs.getBoolean("comments_enabled", true);
        repliesEnabled = prefs.getBoolean("replies_enabled", true);
        spoilersEnabled = prefs.getBoolean("spoilers_enabled", true);
        profanityFilterEnabled = prefs.getBoolean("profanity_filter_enabled", true);
        announcementEnabled = prefs.getBoolean("announcement_enabled", false);
        announcementText = prefs.getString("announcement_text", "");
        announcementType = prefs.getString("announcement_type", "info");
        announcementLink = prefs.getString("announcement_link", "");
        maintenanceMode = prefs.getBoolean("maintenance_mode", false);
        maintenanceMessage = prefs.getString("maintenance_message", "التطبيق يخضع للصيانة الدورية لتحسين الخدمة، سنعود قريباً!");
        appUpdateAvailable = prefs.getBoolean("app_update_available", false);
        latestVersionName = prefs.getString("latest_version_name", "1.0.0");
        appUpdateUrl = prefs.getString("app_update_url", "");
    }

    private static void saveToLocal() {
        if (prefs == null) return;
        prefs.edit()
                .putBoolean("comments_enabled", commentsEnabled)
                .putBoolean("replies_enabled", repliesEnabled)
                .putBoolean("spoilers_enabled", spoilersEnabled)
                .putBoolean("profanity_filter_enabled", profanityFilterEnabled)
                .putBoolean("announcement_enabled", announcementEnabled)
                .putString("announcement_text", announcementText)
                .putString("announcement_type", announcementType)
                .putString("announcement_link", announcementLink)
                .putBoolean("maintenance_mode", maintenanceMode)
                .putString("maintenance_message", maintenanceMessage)
                .putBoolean("app_update_available", appUpdateAvailable)
                .putString("latest_version_name", latestVersionName)
                .putString("app_update_url", appUpdateUrl)
                .apply();
    }

    private static void notifyListeners() {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            for (SettingsUpdateListener listener : listeners) {
                try {
                    listener.onSettingsUpdated();
                } catch (Exception ignored) {}
            }
        });
    }
}
