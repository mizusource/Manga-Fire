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

        try {
            if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() == null) {
                // User is not logged in, skip fetching remote settings to avoid Permission Denied logs.
                return;
            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FIREBASE_PATH);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.exists()) return;
                    try {
                        Boolean cEn = snapshot.child("comments_enabled").getValue(Boolean.class);
                        if (cEn != null) commentsEnabled = cEn;

                        Boolean rEn = snapshot.child("replies_enabled").getValue(Boolean.class);
                        if (rEn != null) repliesEnabled = rEn;

                        Boolean sEn = snapshot.child("spoilers_enabled").getValue(Boolean.class);
                        if (sEn != null) spoilersEnabled = sEn;

                        Boolean pEn = snapshot.child("profanity_filter_enabled").getValue(Boolean.class);
                        if (pEn != null) profanityFilterEnabled = pEn;

                        Boolean annEn = snapshot.child("announcement_enabled").getValue(Boolean.class);
                        if (annEn != null) announcementEnabled = annEn;

                        String annText = snapshot.child("announcement_text").getValue(String.class);
                        if (annText != null) announcementText = annText;

                        String annType = snapshot.child("announcement_type").getValue(String.class);
                        if (annType != null) announcementType = annType;

                        String annLink = snapshot.child("announcement_link").getValue(String.class);
                        if (annLink != null) announcementLink = annLink;

                        Boolean mMode = snapshot.child("maintenance_mode").getValue(Boolean.class);
                        if (mMode != null) maintenanceMode = mMode;

                        String mMsg = snapshot.child("maintenance_message").getValue(String.class);
                        if (mMsg != null) maintenanceMessage = mMsg;

                        Boolean updAvail = snapshot.child("app_update_available").getValue(Boolean.class);
                        if (updAvail != null) appUpdateAvailable = updAvail;

                        String verName = snapshot.child("latest_version_name").getValue(String.class);
                        if (verName != null) latestVersionName = verName;

                        String updUrl = snapshot.child("app_update_url").getValue(String.class);
                        if (updUrl != null) appUpdateUrl = updUrl;

                        saveToLocal();
                        notifyListeners();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing admin settings", e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e(TAG, "Firebase settings listener cancelled: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error attaching Firebase listener", e);
        }
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
