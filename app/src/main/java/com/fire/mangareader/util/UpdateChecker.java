package com.fire.mangareader.util;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    // سنقوم بتغيير هذا الرابط لاحقاً لرابط ملفك على جيتهاب
    private static final String UPDATE_JSON_URL = "https://raw.githubusercontent.com/mizusource/Manga-Fire/refs/heads/main/update.json";

    public static void checkForUpdates(Activity activity) {
        new Thread(() -> {
            try {
                // 1. جلب بيانات التحديث من الإنترنت
                URL url = new URL(UPDATE_JSON_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000); // 5 ثواني كحد أقصى للاتصال

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // 2. تحليل البيانات (JSON)
                JSONObject json = new JSONObject(response.toString());
                int latestVersionCode = json.getInt("versionCode");
                String latestVersionName = json.getString("versionName");
                String updateMessage = json.getString("updateMessage");
                String apkUrl = json.getString("apkUrl");

                // 3. معرفة إصدار التطبيق الحالي المثبت على هاتف المستخدم
                PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                int currentVersionCode = pInfo.versionCode;

                // 4. المقارنة وإظهار النافذة إذا كان هناك تحديث
                if (latestVersionCode > currentVersionCode) {
                    new Handler(Looper.getMainLooper()).post(() -> 
                        showUpdateDialog(activity, latestVersionName, updateMessage, apkUrl)
                    );
                }
            } catch (Exception e) {
                Log.e("UpdateChecker", "فشل فحص التحديثات: " + e.getMessage());
            }
        }).start();
    }

    private static void showUpdateDialog(Activity activity, String versionName, String updateMessage, String apkUrl) {
        new AlertDialog.Builder(activity)
                .setTitle("تحديث جديد متوفر! 🚀")
                .setMessage("الإصدار المتاح: " + versionName + "\n\n" + updateMessage)
                .setCancelable(false) // يمنع إغلاق النافذة بالنقر خارجها
                .setPositiveButton("تحديث الآن", (dialog, which) -> downloadUpdate(activity, apkUrl, versionName))
                .setNegativeButton("لاحقاً", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private static void downloadUpdate(Context context, String apkUrl, String versionName) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
            request.setTitle("تحديث Manga Fire");
            request.setDescription("جاري تنزيل الإصدار " + versionName + "...");
            
            // حفظ الملف في مجلد التنزيلات العام في الهاتف
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MangaFire_v" + versionName + ".apk");
            
            // إظهار الإشعار أثناء التنزيل وبعد اكتماله ليتمكن المستخدم من النقر عليه لتثبيته
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
                Toast.makeText(context, "بدأ تنزيل التحديث! اسحب شريط الإشعارات للأعلى لمتابعة التحميل.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "حدث خطأ أثناء محاولة التنزيل", Toast.LENGTH_SHORT).show();
        }
    }
}
