package com.fire.mangareader.util;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public final class ImgBbUploader {
    private static final String TAG = "ImgBbUploader";
    private static final String API_KEY = "48109139446b79cd00f0c368159a2e31";
    private static final String UPLOAD_URL = "https://api.imgbb.com/1/upload";

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    public static void uploadImage(Context context, Uri imageUri, UploadCallback callback) {
        new Thread(() -> {
            try {
                InputStream is = context.getContentResolver().openInputStream(imageUri);
                if (is == null) {
                    callback.onError("تعذر قراءة ملف الصورة");
                    return;
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                int len;
                while ((len = is.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                is.close();

                String base64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

                FormBody body = new FormBody.Builder()
                        .add("key", API_KEY)
                        .add("image", base64)
                        .build();

                Request req = new Request.Builder()
                        .url(UPLOAD_URL)
                        .post(body)
                        .build();

                Response response = MangaOkHttp.getClient().newCall(req).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    response.close();
                    JSONObject obj = new JSONObject(json);
                    if (obj.optBoolean("success")) {
                        JSONObject data = obj.getJSONObject("data");
                        String displayUrl = data.optString("display_url", data.optString("url", ""));
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                            callback.onSuccess(displayUrl);
                        });
                        return;
                    }
                }
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError("فشل رفع الصورة إلى السيرفر");
                });
            } catch (Exception e) {
                Log.e(TAG, "Upload failed", e);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError(e.getMessage());
                });
            }
        }).start();
    }
}
