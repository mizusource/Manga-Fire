package com.fire.mangareader.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.presentation.activity.ChapterReaderActivity;
import com.fire.mangareader.data.database.DownloadedChapter;
import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {

    private Context context;
    private List<DownloadedChapter> downloadedList;

    public DownloadAdapter(Context context, List<DownloadedChapter> downloadedList) {
        this.context = context;
        this.downloadedList = downloadedList;
    }

    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // إنشاء تصميم برمجي للبطاقة لتجنب الأخطاء
        android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
        layout.setLayoutParams(new androidx.recyclerview.widget.RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(32, 24, 32, 24);
        
        com.google.android.material.card.MaterialCardView card = new com.google.android.material.card.MaterialCardView(context);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 8, 16, 8);
        card.setLayoutParams(params);
        card.setCardBackgroundColor(android.graphics.Color.parseColor("#1E1E1E"));
        card.setRadius(16f);
        
        TextView tvMangaTitle = new TextView(context);
        tvMangaTitle.setId(View.generateViewId());
        tvMangaTitle.setTextColor(android.graphics.Color.WHITE);
        tvMangaTitle.setTextSize(16f);
        tvMangaTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        
        TextView tvChapterTitle = new TextView(context);
        tvChapterTitle.setId(View.generateViewId());
        tvChapterTitle.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // لون أخضر للتنزيلات
        tvChapterTitle.setTextSize(14f);
        tvChapterTitle.setPadding(0, 8, 0, 0);

        layout.addView(tvMangaTitle);
        layout.addView(tvChapterTitle);
        card.addView(layout);

        return new DownloadViewHolder(card, tvMangaTitle, tvChapterTitle);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
        DownloadedChapter chapter = downloadedList.get(position);
        
        // استخراج اسم المانجا من الرابط (مؤقتاً للتبسيط)
        String mangaName = "";
        try {
            android.net.Uri uri = android.net.Uri.parse(chapter.mangaUrl);
            mangaName = uri.getLastPathSegment();
            if (mangaName == null || mangaName.isEmpty()) {
                java.util.List<String> segments = uri.getPathSegments();
                if (segments.size() > 0) mangaName = segments.get(segments.size() - 1);
            }
            if (mangaName != null) mangaName = mangaName.replace("-", " ");
            else mangaName = "Unknown Manga";
        } catch (Exception e) {
            mangaName = "Unknown Manga";
        }
        
        holder.tvMangaTitle.setText(mangaName.toUpperCase());
        holder.tvChapterTitle.setText(chapter.chapterTitle);

        // عند الضغط، فتح شاشة القراءة (النظام الذكي سيتعرف عليه ويفتحه بدون إنترنت)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChapterReaderActivity.class);
            intent.putExtra("chapterUrl", chapter.chapterUrl);
            intent.putExtra("mangaUrl", chapter.mangaUrl);
            intent.putExtra("chapterTitle", chapter.chapterTitle);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return downloadedList != null ? downloadedList.size() : 0;
    }

    static class DownloadViewHolder extends RecyclerView.ViewHolder {
        TextView tvMangaTitle, tvChapterTitle;

        public DownloadViewHolder(@NonNull View itemView, TextView title, TextView chapter) {
            super(itemView);
            tvMangaTitle = title;
            tvChapterTitle = chapter;
        }
    }
}
