package com.fire.mangareader.domain.usecase.update;

import android.os.Handler;
import android.os.Looper;

import com.fire.mangareader.domain.model.remote.Update;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateUseCase {

    public interface Callback {
        void onUpdateAvailable(Update update);
        void onNoUpdate();
        void onError(String error);
    }

    public void checkForUpdate(long currentVersion, Callback callback) {
        new Thread(() -> {
            try {
                // In MangaSlayer it was: https://api.github.com/repos/abdlhay/Manga_Slayer/releases/latest
                // We use a generic or real URL here
                URL url = new URL("https://api.github.com/repos/abdlhay/Manga_Slayer/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();

                    JSONObject jsonResponse = new JSONObject(content.toString());
                    String tagName = jsonResponse.optString("tag_name", "0");
                    String body = jsonResponse.optString("body", "");
                    
                    // Basic parsing for version. In real app, tag_name can be v1.2.3 -> convert to long
                    long latestVersion = 0;
                    try {
                        String versionStr = tagName.replaceAll("[^0-9]", "");
                        if (!versionStr.isEmpty()) {
                            latestVersion = Long.parseLong(versionStr);
                        }
                    } catch (Exception e) {}

                    if (latestVersion > currentVersion) {
                        long fileSize = 0;
                        String downloadUrl = "";
                        if (jsonResponse.has("assets")) {
                            org.json.JSONArray assets = jsonResponse.getJSONArray("assets");
                            if (assets.length() > 0) {
                                JSONObject firstAsset = assets.getJSONObject(0);
                                fileSize = firstAsset.optLong("size", 0);
                                downloadUrl = firstAsset.optString("browser_download_url", "");
                            }
                        }

                        Update update = new Update(latestVersion, downloadUrl, "تحديث جديد متوفر!", body, fileSize, false);
                        new Handler(Looper.getMainLooper()).post(() -> callback.onUpdateAvailable(update));
                    } else {
                        new Handler(Looper.getMainLooper()).post(callback::onNoUpdate);
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("فشل الاتصال بالخادم"));
                }
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("حدث خطأ أثناء التحقق من التحديث: " + e.getMessage()));
            }
        }).start();
    }
}
