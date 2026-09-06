import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r") as f:
    content = f.read()

target = """                AppDatabase.getInstance(this).chapterStateDao().insert(state);
                
                // Add to Supabase Read History"""

replacement = """                AppDatabase.getInstance(this).chapterStateDao().insert(state);
                com.fire.mangareader.data.local.DatabaseBridge.addRecent(this, mangaUrl, mangaTitle, mangaCover, chapterUrl, chapterTitle);
                
                // Add to Supabase Read History"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w") as f:
    f.write(content)

