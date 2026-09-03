import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

content = content.replace(
    '''onChapterClick = { chapterId: String, mangaId: String, chapterTitle: String, mangaTitle: String, mangaCover: String -> 
                                    val intent = Intent(this@MainComposeActivity, ChapterReaderActivity::class.java).apply {
                                        putExtra("chapterUrl", chapterId)
                                        putExtra("mangaUrl", mangaId)
                                        putExtra("chapterTitle", chapterTitle)
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                }''',
    '''onChapterClick = { chapterId -> navController.navigate("reader/$chapterId") }'''
)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
