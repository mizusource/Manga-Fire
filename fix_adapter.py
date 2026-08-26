with open("app/src/main/java/com/fire/mangareader/adapter/ChapterAdapter.java", "r") as f:
    content = f.read()

import_str = """import com.fire.mangareader.activity.DownloadQualityDialog;
import com.fire.mangareader.service.DownloadService;"""
if "DownloadQualityDialog" not in content:
    content = content.replace("import com.fire.mangareader.utils.MangaDownloader;", "import com.fire.mangareader.utils.MangaDownloader;\n" + import_str)

old_logic = """                    } else {
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
                    }"""

new_logic = """                    } else {
                        DownloadQualityDialog.show(context, quality -> {
                            Toast.makeText(context, "بدء التحميل في الخلفية...", Toast.LENGTH_SHORT).show();
                            DownloadService.startDownload(context, mangaUrl, chapter.getUrl(), chapter.getTitle());
                            
                            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                                holder.downloadProgress.setVisibility(View.GONE);
                            });
                        });
                    }"""

content = content.replace(old_logic, new_logic)

with open("app/src/main/java/com/fire/mangareader/adapter/ChapterAdapter.java", "w") as f:
    f.write(content)
