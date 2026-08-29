import re

with open('app/src/main/java/com/fire/mangareader/activity/ChapterReaderActivity.java', 'r') as f:
    content = f.read()

new_save = """
    private void saveReadingProgress(int currentPage, int totalPages) {
        new Thread(() -> {
            try {
                com.fire.mangareader.database.ChapterState state = AppDatabase.getInstance(this).chapterStateDao().getChapterState(chapterUrl);
                if (state == null) {
                    state = new com.fire.mangareader.database.ChapterState();
                    state.chapterUrl = chapterUrl;
                    state.mangaUrl = mangaUrl;
                }
                state.lastPage = currentPage - 1;
                state.isRead = true;
                if (currentPage == totalPages && totalPages > 0) {
                    state.isCompleted = true;
                }
                AppDatabase.getInstance(this).chapterStateDao().insert(state);
                
                // Add to Supabase Read History
                com.fire.mangareader.network.SupabaseManager.getInstance(this).markChapterRead(mangaUrl, chapterUrl, chapterTitle, null);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
"""

content = re.sub(r'private void saveReadingProgress\(int currentPage, int totalPages\)\s*\{.*?\n    \}', new_save.strip(), content, flags=re.DOTALL)

with open('app/src/main/java/com/fire/mangareader/activity/ChapterReaderActivity.java', 'w') as f:
    f.write(content)
