package com.fire.mangareader.domain.usecase.update;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUpdateUseCase {

    public interface DownloadProgressCallback {
        void onProgress(int progress, long bytesDownloaded, long totalBytes);
        void onComplete(File downloadedFile);
        void onError(String error);
    }

    public void downloadUpdate(String downloadUrl, File outputFile, DownloadProgressCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    String responseMessage = connection.getResponseMessage();
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Server returned HTTP " + responseCode + " " + responseMessage));
                    return;
                }

                long fileLength = connection.getContentLength();
                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(outputFile);

                byte[] data = new byte[4096];
                long total = 0;
                int count;
                Handler mainHandler = new Handler(Looper.getMainLooper());
                
                long lastProgressTime = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);

                    if (fileLength > 0) {
                        int progress = (int) (total * 100 / fileLength);
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastProgressTime > 200) { // Update progress UI every 200ms
                            lastProgressTime = currentTime;
                            long finalTotal = total;
                            mainHandler.post(() -> callback.onProgress(progress, finalTotal, fileLength));
                        }
                    }
                }

                output.flush();
                output.close();
                input.close();

                mainHandler.post(() -> callback.onComplete(outputFile));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("فشل التحميل: " + e.getMessage()));
            }
        }).start();
    }
}
