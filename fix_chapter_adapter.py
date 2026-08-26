with open("app/src/main/java/com/fire/mangareader/adapter/ChapterAdapter.java", "r") as f:
    content = f.read()

import_str = """import com.fire.mangareader.activity.DownloadQualityDialog;
import com.fire.mangareader.service.DownloadService;"""

if "DownloadQualityDialog" not in content:
    content = content.replace("import com.fire.mangareader.utils.MangaDownloader;", "import com.fire.mangareader.utils.MangaDownloader;\n" + import_str)

old_download = """                    // نظام التنزيل والحذف
                    if (isDownloaded) {"""

# Let's find exactly how the popup items are added
#             popup.getMenu().add(0, 1, 0, isDownloaded ? "حذف التنزيل" : "تنزيل الفصل");
#             popup.getMenu().add(0, 2, 0, isRead ? "تحديد كغير مقروء" : "تحديد كمقروء");

# The action for 1 is:
logic = """                if (item.getItemId() == 1) { 
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
                            DownloadService.startDownload(context, mangaUrl, chapter.getUrl(), chapter.getTitle());
                            
                            // Optimistically mark as downloaded for UI immediately
                            new Handler(Looper.getMainLooper()).post(() -> {
                                holder.downloadProgress.setVisibility(View.GONE);
                            });
                        });
                    }
                }"""

# I need to replace the entire block for `if (item.getItemId() == 1) { ... } else if (item.getItemId() == 2)`
