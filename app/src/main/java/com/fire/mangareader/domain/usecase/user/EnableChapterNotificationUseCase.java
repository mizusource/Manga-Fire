package com.fire.mangareader.domain.usecase.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.fire.mangareader.data.network.SupabaseManager;
import com.google.firebase.messaging.FirebaseMessaging;

public class EnableChapterNotificationUseCase {
    private final Context context;

    public EnableChapterNotificationUseCase(Context context) {
        this.context = context;
    }

    public interface Callback {
        void onSuccess(String token);
        void onError(String error);
    }

    public void enableNewChapterNotification(boolean enable, Callback callback) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("notifications_enabled", enable).apply();

        if (enable) {
            FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        callback.onError("فشل جلب توكن الإشعارات");
                        return;
                    }
                    String token = task.getResult();
                    // Here we would typically send this token to our backend (Supabase or custom server)
                    // so it knows where to send the push notifications.
                    if (SupabaseManager.getInstance(context).isLoggedIn()) {
                         // Send to Supabase
                         // SupabaseManager.getInstance(context).updateUserFcmToken(token);
                    }
                    callback.onSuccess(token);
                });
        } else {
            // Can optionally delete token
             FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                 callback.onSuccess("");
             });
        }
    }
}
