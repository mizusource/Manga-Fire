package com.fire.mangareader.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TelegramManager {
    private static final String TAG = "TelegramManager";
    public static final String DEFAULT_BOT_TOKEN = "8944511107:AAFPA2OSAJVVBHGGrzZUdghsnpIRrqFOqWA";
    public static final String DEFAULT_CHANNEL_ID = "-1003627092623";

    private final SharedPreferences prefs;

    public interface TelegramCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public TelegramManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences("telegram_prefs", Context.MODE_PRIVATE);
    }

    public String getBotToken() {
        return prefs.getString("bot_token", DEFAULT_BOT_TOKEN);
    }

    public void setBotToken(String token) {
        prefs.edit().putString("bot_token", token.trim()).apply();
    }

    public String getChannelId() {
        return prefs.getString("channel_id", DEFAULT_CHANNEL_ID);
    }

    public void setChannelId(String channelId) {
        prefs.edit().putString("channel_id", channelId.trim()).apply();
    }

    public static void openTelegramChannel(Context context, String channelUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(channelUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Could not open Telegram channel", e);
        }
    }

    public void uploadFile(File file, String caption, TelegramCallback callback) {
        new Thread(() -> {
            try {
                String token = getBotToken();
                String channel = getChannelId();
                if (token.isEmpty() || channel.isEmpty()) {
                    callback.onError("بيانات التليجرام غير مضبوطة");
                    return;
                }

                RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("chat_id", channel)
                        .addFormDataPart("caption", caption != null ? caption : "")
                        .addFormDataPart("parse_mode", "HTML")
                        .addFormDataPart("document", file.getName(), fileBody);

                Request req = new Request.Builder()
                        .url("https://api.telegram.org/bot" + token + "/sendDocument")
                        .post(builder.build())
                        .build();

                Response response = MangaOkHttp.getClient().newCall(req).execute();
                if (response.isSuccessful()) {
                    response.close();
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        callback.onSuccess("تم إرسال الملف بنجاح إلى تليجرام!");
                    });
                } else {
                    String err = response.body() != null ? response.body().string() : ("Error " + response.code());
                    response.close();
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        callback.onError("فشل الإرسال: " + err);
                    });
                }
            } catch (Exception e) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError(e.getMessage());
                });
            }
        }).start();
    }
}
