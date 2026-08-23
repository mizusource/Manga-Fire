package com.fire.mangareader.adapter;

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
import com.fire.mangareader.activity.ChapterReaderActivity;
import com.fire.mangareader.database.AppDatabase;
import com.fire.mangareader.model.Chapter;
import com.fire.mangareader.utils.MangaDownloader; 
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private Context context;
    private List<Chapter> chapters;
    private String mangaUrl;
    private List<String> readChapters = new ArrayList<>();
    private List<String> downloadedChapters = new ArrayList<>();

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
        boolean isDownloaded = downloadedChapters != null && downloadedChapters.contains(chapter.getUrl());

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
            context.startActivity(intent);
        });

        // زر الخيارات (النقاط الثلاث)
        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMenu);
            popup.getMenu().add(0, 1, 0, isDownloaded ? "حذف التنزيل" : "تنزيل الفصل");
            popup.getMenu().add(0, 2, 0, isRead ? "تحديد كغير مقروء" : "تحديد كمقروء");
            
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
                        holder.downloadProgress.setVisibility(View.VISIBLE);
                        holder.downloadProgress.setProgress(0);
                        holder.tvChapterDate.setText("جاري التجهيز...");
                        holder.tvChapterDate.setTextColor(Color.parseColor("#FF9800")); 

                        MangaDownloader.downloadChapter(context, mangaUrl, chapter.getUrl(), chapter.getTitle(), new MangaDownloader.DownloadListener() {
                            @Override
                            public void onProgressUpdate(int current, int total) {
                                holder.downloadProgress.setMax(total);
                                holder.downloadProgress.setProgress(current);
                                holder.tvChapterDate.setText("جاري التنزيل: " + current + " / " + total);
                            }

                            @Override
                            public void onSuccess() {
                                holder.downloadProgress.setVisibility(View.GONE);
                                downloadedChapters.add(chapter.getUrl());
                                int adapterPos = holder.getBindingAdapterPosition(); if (adapterPos != androidx.recyclerview.widget.RecyclerView.NO_POSITION) notifyItemChanged(adapterPos); 
                            }

                            @Override
                            public void onError(String errorMessage) {
                                holder.downloadProgress.setVisibility(View.GONE);
                                holder.tvChapterDate.setText("خطأ في التنزيل");
                                holder.tvChapterDate.setTextColor(Color.RED);
                            }
                        });
                    }
                } else if (item.getItemId() == 2) {
                    // 🚀 نظام التحديد كمقروء / غير مقروء
                    boolean newState = !isRead;
                    new Thread(() -> {
                        try {
                            // حفظ الحالة في قاعدة البيانات
                            com.fire.mangareader.database.ChapterState state = new com.fire.mangareader.database.ChapterState();
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
                }
                return true;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return chapters != null ? chapters.size() : 0;
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
