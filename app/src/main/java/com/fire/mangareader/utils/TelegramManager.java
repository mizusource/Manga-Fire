package com.fire.mangareader.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.content.SharedPreferences;

public class TelegramManager {
    
    private Context context;
    private SharedPreferences prefs;

    public TelegramManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
    }

    public static void openTelegramChannel(Context context, String channelName) {
        String webUri = channelName;
        if (!webUri.startsWith("http")) {
             webUri = "https://t.me/" + channelName;
        }
        
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
        context.startActivity(intent);
    }
    
    public String getBotToken() {
        return prefs.getString("bot_token", "");
    }
}
