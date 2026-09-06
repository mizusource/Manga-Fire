import re

with open("app/src/main/java/com/fire/mangareader/util/MangaDownloader.java", "r") as f:
    content = f.read()

new_download = """
    public static void downloadChapter(Context context, String mangaUrl, String chapterUrl, String chapterTitle, DownloadListener listener) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        
        com.fire.mangareader.data.network.MangaScraper.fetchChapterPages(chapterUrl, new com.fire.mangareader.data.network.MangaScraper.ChapterPagesCallback() {
            @Override
            public void onSuccess(java.util.List<String> imageUrls) {
                new Thread(() -> {
                    try {
                        if (isCancelled) return;
                        if (imageUrls == null || imageUrls.isEmpty()) {
                            mainHandler.post(() -> {
                                if (listener != null) listener.onError("لا يوجد صور في الفصل");
                            });
                            return;
                        }

                        java.io.File mangaFolder = new java.io.File(context.getFilesDir(), String.valueOf(mangaUrl.hashCode()));
                        java.io.File chapterFolder = new java.io.File(mangaFolder, String.valueOf(chapterUrl.hashCode()));
                        if (!chapterFolder.exists() && !chapterFolder.mkdirs()) {
                            throw new Exception("لا يمكن إنشاء مجلد الحفظ");
                        }

                        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(4);
                        java.util.List<java.util.concurrent.Future<?>> futures = new java.util.ArrayList<>();
                        java.util.concurrent.atomic.AtomicInteger count = new java.util.concurrent.atomic.AtomicInteger(0);
                        
                        for (int i = 0; i < imageUrls.size(); i++) {
                            final int index = i;
                            futures.add(executor.submit(() -> {
                                if (isCancelled) return;
                                try {
                                    downloadImageFile(context, imageUrls.get(index), chapterUrl, new java.io.File(chapterFolder, index + ".jpg"));
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                                int currentProgress = count.incrementAndGet();
                                int total = imageUrls.size();
                                if (listener != null) {
                                    mainHandler.post(() -> listener.onProgressUpdate(currentProgress, total));
                                }
                            }));
                        }
                        
                        for (java.util.concurrent.Future<?> f : futures) {
                            try { f.get(); } catch (Exception ignored) {}
                        }
                        executor.shutdown();

                        com.fire.mangareader.data.database.DownloadedChapter downloaded = new com.fire.mangareader.data.database.DownloadedChapter();
                        downloaded.chapterUrl = chapterUrl;
                        downloaded.mangaUrl = mangaUrl;
                        downloaded.chapterTitle = chapterTitle;
                        downloaded.localFolderPath = chapterFolder.getAbsolutePath();

                        new com.fire.mangareader.domain.usecase.downloads.AddMangaDownloadUseCase(context).execute(downloaded, new com.fire.mangareader.domain.usecase.downloads.AddMangaDownloadUseCase.Callback() {
                            @Override
                            public void onSuccess() {}
                            @Override
                            public void onError(String error) {}
                        });

                        mainHandler.post(() -> {
                            if (listener != null) listener.onSuccess();
                            android.widget.Toast.makeText(context, "تم تنزيل: " + chapterTitle, android.widget.Toast.LENGTH_SHORT).show();
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        mainHandler.post(() -> {
                            if (listener != null) listener.onError("فشل: " + e.getMessage());
                        });
                    }
                }).start();
            }

            @Override
            public void onError(String errorMessage) {
                mainHandler.post(() -> {
                    if (listener != null) listener.onError(errorMessage);
                });
            }
        });
    }
"""

content = re.sub(r'public static void downloadChapter\(Context context, String mangaUrl, String chapterUrl, String chapterTitle, DownloadListener listener\) \{.*?\}\n    // 🚀', new_download.strip() + '\n\n    // 🚀', content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/util/MangaDownloader.java", "w") as f:
    f.write(content)

