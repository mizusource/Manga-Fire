package com.fire.mangareader.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.presentation.activity.ChapterReaderActivity;
import com.fire.mangareader.data.database.AppDatabase;
import com.fire.mangareader.domain.model.Chapter;
import com.fire.mangareader.util.MangaDownloader;

import com.fire.mangareader.presentation.activity.DownloadQualityDialog;
import com.fire.mangareader.data.service.DownloadService;
import com.fire.mangareader.core.di.DownloadModule;
import com.fire.mangareader.data.download.DownloadManager;
 
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private java.util.Map<String, Integer> downloadProgresses = new java.util.HashMap<>();
    
    public void setDownloadProgresses(java.util.Map<String, Integer> progresses) {
        this.downloadProgresses = progresses;
        notifyDataSetDataSetChanged(); // Simple refresh for now, or could use DiffUtil
    }
    
    private void notifyDataSetDataSetChanged() {
        notifyDataSetChanged();
    }

    private Context context;
    private List<Chapter> chapters;
    private String mangaUrl;
    private List<String> readChapters = new ArrayList<>();
    private List<String> downloadedChapters = new ArrayList<>();
    private int displayLimit = 50;

    public void setDisplayLimit(int limit) {
        this.displayLimit = limit;
        notifyDataSetChanged();
    }

    private String mangaTitle, mangaCover;

    public ChapterAdapter(Context context, List<Chapter> chapters, String mangaUrl, String mangaTitle, String mangaCover) {
        this.context = context;
        this.chapters = chapters;
        this.mangaUrl = mangaUrl;
        this.mangaTitle = mangaTitle;
        this.mangaCover = mangaCover;
    }

    public void setReadChapters(List<String> readChapters) {
        this.readChapters = readChapters;
        notifyDataSetChanged();
    }

    public void setDownloadedChapters(List<String> downloadedChapters) {
        this.downloadedChapters = downloadedChapters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapters.get(position);
        holder.tvChapterTitle.setText(chapter.getTitle());

        boolean isRead = readChapters != null && readChapters.contains(chapter.getUrl());
        boolean isDownloaded = (downloadedChapters != null && downloadedChapters.contains(chapter.getUrl())) || com.fire.mangareader.util.DownloadChecker.isChapterDownloaded(context, mangaUrl, chapter.getUrl());

        holder.downloadProgress.setVisibility(View.GONE);

        // تخصيص الألوان
        if (isRead) {
            holder.tvChapterTitle.setTextColor(Color.GRAY);
            holder.readIndicator.setBackgroundColor(Color.GRAY);
            holder.tvChapterDate.setText("تمت القراءة");
        } else {
            holder.tvChapterTitle.setTextColor(Color.WHITE);
            holder.readIndicator.setBackgroundColor(Color.parseColor("#4CAF50")); 
            holder.tvChapterDate.setText("متاح للقراءة");
        }

        if (isDownloaded) {
            holder.tvChapterDate.setText("تم التنزيل");
            holder.tvChapterDate.setTextColor(Color.parseColor("#2196F3")); 
        } else if (!isRead) {
            holder.tvChapterDate.setTextColor(Color.GRAY);
        }

        // فتح القارئ
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChapterReaderActivity.class);
            intent.putExtra("chapterUrl", chapter.getUrl());
            intent.putExtra("mangaUrl", mangaUrl);
            intent.putExtra("mangaTitle", mangaTitle);
            intent.putExtra("mangaCover", mangaCover);
            intent.putExtra("chapterTitle", chapter.getTitle());
            
            if (context instanceof android.app.Activity) {
                androidx.core.app.ActivityOptionsCompat options = androidx.core.app.ActivityOptionsCompat.makeCustomAnimation(
                        context, android.R.anim.fade_in, android.R.anim.fade_out);
                context.startActivity(intent, options.toBundle());
            } else {
                context.startActivity(intent);
            }
        });

        // زر الخيارات (النقاط الثلاث)
        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMenu);
            popup.getMenu().add(0, 1, 0, isDownloaded ? "حذف التنزيل" : "تنزيل الفصل");
            popup.getMenu().add(0, 2, 0, isRead ? "تحديد كغير مقروء" : "تحديد كمقروء");
            popup.getMenu().add(0, 3, 0, "📄 تصدير كـ PDF");
            popup.getMenu().add(0, 4, 0, "📦 تصدير كـ CBZ");
            
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) { 
                    // نظام التنزيل والحذف
                    if (isDownloaded) {
                        new Thread(() -> {
                            try {
                                File mangaFolder = new File(context.getFilesDir(), String.valueOf(mangaUrl.hashCode()));
                                File chapterFolder = new File(mangaFolder, String.valueOf(chapter.getUrl().hashCode()));

                                if (chapterFolder.exists() && chapterFolder.isDirectory()) {
                                    File[] files = chapterFolder.listFiles();
                                    if (files != null) {
                                        for (File f : files) f.delete(); 
                                    }
                                    chapterFolder.delete(); 
                                }

                                AppDatabase.getInstance(context).downloadDao().deleteByUrl(chapter.getUrl());

                                new Handler(Looper.getMainLooper()).post(() -> {
                                    downloadedChapters.remove(chapter.getUrl());
                                    int adapterPos = holder.getBindingAdapterPosition(); if (adapterPos != androidx.recyclerview.widget.RecyclerView.NO_POSITION) notifyItemChanged(adapterPos); 
                                    Toast.makeText(context, "تم حذف الفصل لتوفير المساحة 🗑️", Toast.LENGTH_SHORT).show();
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, "حدث خطأ أثناء الحذف", Toast.LENGTH_SHORT).show());
                            }
                        }).start();
                        
                    } else {
                        DownloadQualityDialog.show(context, quality -> {
                            Toast.makeText(context, "بدء التحميل في الخلفية...", Toast.LENGTH_SHORT).show();
                            DownloadModule.getInstance(context).provideDownloadManager().startDownload(mangaUrl, chapter.getUrl(), chapter.getTitle());
                            
                            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                                holder.downloadProgress.setVisibility(View.GONE);
                            });
                        });
                    }
                } else if (item.getItemId() == 2) {
                    // 🚀 نظام التحديد كمقروء / غير مقروء
                    boolean newState = !isRead;
                    new Thread(() -> {
                        try {
                            // حفظ الحالة في قاعدة البيانات
                            com.fire.mangareader.data.database.ChapterState state = new com.fire.mangareader.data.database.ChapterState();
                            state.chapterUrl = chapter.getUrl();
                            state.mangaUrl = mangaUrl;
                            state.isRead = newState;
                            
                            AppDatabase.getInstance(context).chapterStateDao().insert(state);

                            // تحديث الواجهة فوراً
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (newState) {
                                    readChapters.add(chapter.getUrl());
                                    Toast.makeText(context, "تم التحديد كمقروء ✔️", Toast.LENGTH_SHORT).show();
                                } else {
                                    readChapters.remove(chapter.getUrl());
                                    Toast.makeText(context, "تم إزالة علامة القراءة ✖️", Toast.LENGTH_SHORT).show();
                                }
                                int adapterPos = holder.getBindingAdapterPosition(); if (adapterPos != androidx.recyclerview.widget.RecyclerView.NO_POSITION) notifyItemChanged(adapterPos); // تحديث لون البطاقة
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                } else if (item.getItemId() == 3) {
                    // 📄 تصدير كـ PDF
                    handleChapterExport(chapter, com.fire.mangareader.util.ExportFormat.PDF);
                } else if (item.getItemId() == 4) {
                    // 📦 تصدير كـ CBZ
                    handleChapterExport(chapter, com.fire.mangareader.util.ExportFormat.CBZ);
                }
                return true;
            });
            popup.show();
        });
    }

    private void handleChapterExport(Chapter chapter, com.fire.mangareader.util.ExportFormat format) {
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(context);
        progressDialog.setTitle("تصدير " + chapter.getTitle());
        progressDialog.setMessage("جاري تجهيز صفحات الفصل وتصديره كـ " + format.name() + "...");
        progressDialog.setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.show();

        // 1. فحص إذا كان الفصل محمل محلياً
        File mangaFolder = new File(context.getFilesDir(), String.valueOf(mangaUrl.hashCode()));
        File chapterFolder = new File(mangaFolder, String.valueOf(chapter.getUrl().hashCode()));

        List<String> pagePaths = new ArrayList<>();
        if (chapterFolder.exists() && chapterFolder.isDirectory()) {
            File[] files = chapterFolder.listFiles();
            if (files != null && files.length > 0) {
                java.util.Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
                for (File f : files) {
                    if (f.isFile() && (f.getName().endsWith(".jpg") || f.getName().endsWith(".png") || f.getName().endsWith(".webp"))) {
                        pagePaths.add(f.getAbsolutePath());
                    }
                }
            }
        }

        com.fire.mangareader.util.ChapterExportManager.ExportCallback callback = new com.fire.mangareader.util.ChapterExportManager.ExportCallback() {
            @Override
            public void onProgress(int current, int total, String status) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    progressDialog.setMax(total);
                    progressDialog.setProgress(current);
                    progressDialog.setMessage(status);
                });
            }

            @Override
            public void onSuccess(File exportedFile, com.fire.mangareader.util.ExportFormat expFormat) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    progressDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("اكتمل التصدير بنجاح 🎉")
                        .setMessage("تم حفظ الملف في مجلد التنزيلات:\n" + exportedFile.getName() + "\nالحجم: " + (exportedFile.length() / 1024) + " KB")
                        .setPositiveButton("فتح الملف", (d, w) -> com.fire.mangareader.util.ChapterExportManager.openFile(context, exportedFile, expFormat))
                        .setNeutralButton("مشاركة", (d, w) -> com.fire.mangareader.util.ChapterExportManager.shareFile(context, exportedFile, chapter.getTitle()))
                        .setNegativeButton("إغلاق", null)
                        .show();
                });
            }

            @Override
            public void onError(String error) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(context, "فشل التصدير: " + error, Toast.LENGTH_LONG).show();
                });
            }
        };

        if (!pagePaths.isEmpty()) {
            com.fire.mangareader.util.ChapterExportManager.exportChapter(context, mangaTitle, chapter.getTitle(), chapter.getUrl(), pagePaths, format, "ORIGINAL", callback);
        } else {
            // التنزيل أولاً ثم التصدير
            MangaDownloader.downloadChapter(context, mangaUrl, chapter.getUrl(), chapter.getTitle(), new MangaDownloader.DownloadListener() {
                @Override
                public void onProgressUpdate(int current, int total) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressDialog.setMax(total);
                        progressDialog.setProgress(current);
                        progressDialog.setMessage("جاري تحميل صفحات الفصل: " + current + " / " + total);
                    });
                }

                @Override
                public void onSuccess() {
                    List<String> dPaths = new ArrayList<>();
                    List<File> downloadedFiles = com.fire.mangareader.util.DownloadChecker.getDownloadedImages(context, mangaUrl, chapter.getUrl());
                    for (File f : downloadedFiles) {
                        dPaths.add(f.getAbsolutePath());
                    }
                    com.fire.mangareader.util.ChapterExportManager.exportChapter(context, mangaTitle, chapter.getTitle(), chapter.getUrl(), dPaths, format, "ORIGINAL", callback);
                }

                @Override
                public void onError(String errorMessage) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(context, "فشل تجهيز صفحات الفصل: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }

@Override
    public int getItemCount() {
        return chapters != null ? Math.min(chapters.size(), displayLimit) : 0;
    }

    static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvChapterTitle, tvChapterDate;
        View readIndicator;
        ProgressBar downloadProgress;
        ImageButton btnMenu;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapterTitle = itemView.findViewById(R.id.tvChapterTitle);
            tvChapterDate = itemView.findViewById(R.id.tvChapterDate);
            readIndicator = itemView.findViewById(R.id.readIndicator);
            downloadProgress = itemView.findViewById(R.id.downloadProgress);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }
    }
}
