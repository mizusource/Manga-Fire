package com.fire.mangareader.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ChapterDownloader {

    public interface DownloadListener {
        void onProgressUpdate(int current, int total);
        void onComplete(String savedFolderPath);
        void onError(String error);
    }

    public static void downloadChapter(Context context, String mangaTitle, String chapterTitle, List<String> imageUrls, String cookies, String referer, DownloadListener listener) {
        new Thread(() -> {
            try {
                // 1. إنشاء مجلد مخفي للتطبيق داخل ملفات الهاتف
                File appFolder = new File(context.getExternalFilesDir(null), "MangaFire_Downloads");
                File mangaFolder = new File(appFolder, mangaTitle.replaceAll("[^a-zA-Z0-9أ-ي ]", ""));
                File chapterFolder = new File(mangaFolder, chapterTitle.replaceAll("[^a-zA-Z0-9أ-ي ]", ""));

                if (!chapterFolder.exists()) {
                    chapterFolder.mkdirs();
                }

                Handler mainHandler = new Handler(Looper.getMainLooper());

                // 2. تحميل الصور واحدة تلو الأخرى
                for (int i = 0; i < imageUrls.size(); i++) {
                    String imgUrl = imageUrls.get(i);
                    File imageFile = new File(chapterFolder, (i + 1) + ".jpg");

                    // إذا كانت الصورة محملة مسبقاً، تخطاها
                    if (imageFile.exists() && imageFile.length() > 0) {
                        int finalI = i;
                        mainHandler.post(() -> listener.onProgressUpdate(finalI + 1, imageUrls.size()));
                        continue; 
                    }

                    // 3. الاتصال بالموقع مع تمرير الكوكيز والهوية لكسر الحماية!
                    HttpURLConnection connection = (HttpURLConnection) new URL(imgUrl).openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14; SM-A366B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36");
                    if (cookies != null && !cookies.isEmpty()) {
                        connection.setRequestProperty("Cookie", cookies);
                    }
                    if (referer != null) {
                        connection.setRequestProperty("Referer", referer);
                    }
                    
                    connection.connect();

                    if (connection.getResponseCode() == 200) {
                        InputStream input = connection.getInputStream();
                        FileOutputStream output = new FileOutputStream(imageFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                        output.close();
                        input.close();
                    }
                    
                    // تحديث شريط التحميل في الواجهة
                    int finalI = i;
                    mainHandler.post(() -> listener.onProgressUpdate(finalI + 1, imageUrls.size()));
                }

                // 4. إنهاء التحميل بنجاح
                mainHandler.post(() -> listener.onComplete(chapterFolder.getAbsolutePath()));

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> listener.onError(e.getMessage()));
            }
        }).start();
    }
}
