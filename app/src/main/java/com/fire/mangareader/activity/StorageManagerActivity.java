package com.fire.mangareader.activity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fire.mangareader.R;
import com.fire.mangareader.database.AppDatabase;
import com.fire.mangareader.database.DownloadedChapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.util.List;

public class StorageManagerActivity extends AppCompatActivity {

    private TextView tvCacheSize, tvDownloadsSize;
    private ProgressBar storageProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_manager);

        tvCacheSize = findViewById(R.id.tvCacheSize);
        tvDownloadsSize = findViewById(R.id.tvDownloadsSize);
        storageProgress = findViewById(R.id.storageProgress);

        MaterialButton btnClearCache = findViewById(R.id.btnClearCache);
        MaterialButton btnDeleteRead = findViewById(R.id.btnDeleteRead);
        MaterialButton btnClearDownloads = findViewById(R.id.btnClearDownloads);
        SwitchMaterial switchAutoDelete = findViewById(R.id.switchAutoDelete);

        android.content.SharedPreferences prefs = getSharedPreferences("MangaFirePrefs", MODE_PRIVATE);
        switchAutoDelete.setChecked(prefs.getBoolean("auto_delete", false));

        switchAutoDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("auto_delete", isChecked).apply();
        });

        btnClearCache.setOnClickListener(v -> {
            new Thread(() -> {
                com.bumptech.glide.Glide.get(this).clearDiskCache();
                deleteFolder(getCacheDir());
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم مسح الذاكرة المؤقتة", Toast.LENGTH_SHORT).show();
                    calculateSizes();
                });
            }).start();
        });

        btnClearDownloads.setOnClickListener(v -> {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                List<DownloadedChapter> downloads = db.downloadDao().getAllDownloads();
                for (DownloadedChapter chapter : downloads) {
                    deleteFolder(new File(chapter.localFolderPath));
                }
                db.downloadDao().deleteAll();
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم مسح جميع التنزيلات", Toast.LENGTH_SHORT).show();
                    calculateSizes();
                });
            }).start();
        });

        btnDeleteRead.setOnClickListener(v -> {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                List<DownloadedChapter> downloads = db.downloadDao().getAllDownloads();
                List<com.fire.mangareader.database.ChapterState> readStates = db.chapterStateDao().getAllReadStates();
                
                int count = 0;
                for (DownloadedChapter chapter : downloads) {
                    boolean isRead = false;
                    for (com.fire.mangareader.database.ChapterState state : readStates) {
                        if (state.chapterUrl.equals(chapter.chapterUrl) && state.isRead) {
                            isRead = true;
                            break;
                        }
                    }
                    if (isRead) {
                        deleteFolder(new File(chapter.localFolderPath));
                        db.downloadDao().deleteByUrl(chapter.chapterUrl);
                        count++;
                    }
                }
                int finalCount = count;
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم حذف " + finalCount + " فصول مقروءة", Toast.LENGTH_SHORT).show();
                    calculateSizes();
                });
            }).start();
        });

        calculateSizes();
    }

    private void deleteFolder(File folder) {
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) f.delete();
            }
            folder.delete();
        }
    }

    public static void autoCleanOldCache(android.content.Context context) {
        new Thread(() -> {
            try {
                File cacheDir = context.getCacheDir();
                if (cacheDir != null && cacheDir.exists()) {
                    long now = System.currentTimeMillis();
                    long maxAge = 3L * 24 * 60 * 60 * 1000; // 3 days
                    File[] files = cacheDir.listFiles();
                    if (files != null) {
                        for (File f : files) {
                            if (f.isFile() && (now - f.lastModified() > maxAge)) {
                                f.delete();
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }).start();
    }

    private void calculateSizes() {
        new Thread(() -> {
            long cacheSize = getFolderSize(com.bumptech.glide.Glide.getPhotoCacheDir(this)) + getFolderSize(getCacheDir());
            
            long downloadsSize = 0;
            List<DownloadedChapter> downloads = AppDatabase.getInstance(this).downloadDao().getAllDownloads();
            for (DownloadedChapter chapter : downloads) {
                downloadsSize += getFolderSize(new File(chapter.localFolderPath));
            }

            long cacheMb = cacheSize / (1024 * 1024);
            long downMb = downloadsSize / (1024 * 1024);
            long total = cacheMb + downMb;

            runOnUiThread(() -> {
                tvCacheSize.setText("المؤقتة: " + cacheMb + " م.ب");
                tvDownloadsSize.setText("التنزيلات: " + downMb + " م.ب");
                
                if (total > 0) {
                    storageProgress.setProgress((int) ((cacheMb * 100) / total));
                    storageProgress.setSecondaryProgress(100); // the rest is downloads
                } else {
                    storageProgress.setProgress(0);
                    storageProgress.setSecondaryProgress(0);
                }
            });
        }).start();
    }

    private long getFolderSize(File folder) {
        long length = 0;
        if (folder != null && folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        length += file.length();
                    } else {
                        length += getFolderSize(file);
                    }
                }
            }
        }
        return length;
    }
}
