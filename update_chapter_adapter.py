import re

with open("app/src/main/java/com/fire/mangareader/adapter/ChapterAdapter.java", "r") as f:
    content = f.read()

# Replace the download button logic (item 2 in popup menu)
# First we need to import DownloadQualityDialog and DownloadService
imports = """
import com.fire.mangareader.activity.DownloadQualityDialog;
import com.fire.mangareader.service.DownloadService;
"""
content = content.replace("import com.fire.mangareader.utils.MangaDownloader;", "import com.fire.mangareader.utils.MangaDownloader;\n" + imports)

# Find the download option
download_logic_old = """                if (item.getItemId() == 2) {
                    // 📥 تنزيل الفصل
                    Toast.makeText(context, "بدء التنزيل...", Toast.LENGTH_SHORT).show();
                    MangaDownloader.downloadChapter(context, mangaUrl, chapter.getUrl(), chapter.getTitle(), new MangaDownloader.DownloadListener() {
                        @Override
                        public void onProgressUpdate(int current, int total) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                holder.downloadProgress.setVisibility(View.VISIBLE);
                                holder.downloadProgress.setMax(total);
                                holder.downloadProgress.setProgress(current);
                            });
                        }

                        @Override
                        public void onSuccess() {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                holder.downloadProgress.setVisibility(View.GONE);
                                Toast.makeText(context, "اكتمل التنزيل!", Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onError(String errorMessage) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                holder.downloadProgress.setVisibility(View.GONE);
                                Toast.makeText(context, "فشل التنزيل: " + errorMessage, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }"""

download_logic_new = """                if (item.getItemId() == 2) {
                    // 📥 تنزيل الفصل (استدعاء نافذة الجودة ثم خدمة الخلفية)
                    DownloadQualityDialog.show(context, quality -> {
                        Toast.makeText(context, "بدء التحميل في الخلفية...", Toast.LENGTH_SHORT).show();
                        DownloadService.startDownload(context, mangaUrl, chapter.getUrl(), chapter.getTitle());
                    });
                }"""

content = content.replace(download_logic_old, download_logic_new)

with open("app/src/main/java/com/fire/mangareader/adapter/ChapterAdapter.java", "w") as f:
    f.write(content)
