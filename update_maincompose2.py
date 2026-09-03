import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

import_statement = "import com.fire.mangareader.presentation.activity.ChapterReaderActivity\n"
if "ChapterReaderActivity" not in content:
    content = content.replace("import android.os.Bundle", import_statement + "import android.os.Bundle")


content = content.replace(
    'onChapterClick = { chapterId -> navController.navigate("reader/$chapterId") }',
    '''onChapterClick = { chapterId, mangaId, chapterTitle, mangaTitle, mangaCover -> 
                                    val intent = Intent(this@MainComposeActivity, ChapterReaderActivity::class.java).apply {
                                        putExtra("chapterUrl", chapterId)
                                        putExtra("mangaUrl", mangaId)
                                        putExtra("chapterTitle", chapterTitle)
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                }'''
)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)

