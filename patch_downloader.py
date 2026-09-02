import re

filepath = 'app/src/main/java/com/fire/mangareader/util/MangaDownloader.java'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('import com.fire.mangareader.data.database.AppDatabase;', 'import com.fire.mangareader.data.database.AppDatabase;\nimport com.fire.mangareader.domain.usecase.downloads.AddMangaDownloadUseCase;')

old_insert = """                                    DownloadedChapter downloaded = new DownloadedChapter();
                                    downloaded.chapterUrl = chapterUrl;
                                    downloaded.mangaUrl = mangaUrl;
                                    downloaded.chapterTitle = chapterTitle;
                                    downloaded.localFolderPath = chapterFolder.getAbsolutePath();
                                    AppDatabase.getInstance(context).downloadDao().insert(downloaded);"""

new_insert = """                                    DownloadedChapter downloaded = new DownloadedChapter();
                                    downloaded.chapterUrl = chapterUrl;
                                    downloaded.mangaUrl = mangaUrl;
                                    downloaded.chapterTitle = chapterTitle;
                                    downloaded.localFolderPath = chapterFolder.getAbsolutePath();
                                    
                                    new AddMangaDownloadUseCase(context).execute(downloaded, new AddMangaDownloadUseCase.Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError(String error) {
                                        }
                                    });"""

content = content.replace(old_insert, new_insert)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched MangaDownloader.java")
