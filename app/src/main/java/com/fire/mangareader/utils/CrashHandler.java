package com.fire.mangareader.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private final Thread.UncaughtExceptionHandler defaultHandler;
    private final Context context;

    public CrashHandler(Context context) {
        this.context = context;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            // Log to logcat
            Log.e(TAG, "Uncaught exception in thread " + thread.getName(), throwable);

            // Save to file
            saveCrashInfoToFile(throwable);

            // Show Toast (needs to be on UI thread)
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    Toast.makeText(context, "حدث خطأ غير متوقع. جاري حفظ السجل...", Toast.LENGTH_LONG).show();
                } catch (Exception ignored) {}
            });

            // Give the toast a moment to show and file to save
            Thread.sleep(2000);

        } catch (Exception e) {
            Log.e(TAG, "Error while handling crash", e);
        } finally {
            // Pass to default handler (which usually kills the app)
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        }
    }

    private void saveCrashInfoToFile(Throwable ex) {
        try {
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir == null) cacheDir = context.getCacheDir();
            File crashDir = new File(cacheDir, "crashes");
            if (!crashDir.exists()) {
                crashDir.mkdirs();
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File crashFile = new File(crashDir, "crash_" + timeStamp + ".log");

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String crashLog = sw.toString();

            FileWriter writer = new FileWriter(crashFile);
            writer.write("Timestamp: " + timeStamp + "\n");
            writer.write("Exception:\n" + crashLog);
            writer.close();
            
            Log.d(TAG, "Crash saved to: " + crashFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "Failed to save crash log", e);
        }
    }
}
