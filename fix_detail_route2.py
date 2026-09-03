import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

correct_lambda = '''onChapterClick = { chapterId: String, mangaId: String, chapterTitle: String, mangaTitle: String, mangaCover: String -> 
                                    val intent = Intent(this@MainComposeActivity, ChapterReaderActivity::class.java).apply {
                                        putExtra("chapterUrl", chapterId)
                                        putExtra("mangaUrl", mangaId)
                                        putExtra("chapterTitle", chapterTitle)
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                }'''

# Let's fix LibraryScreen
content = re.sub(r'LibraryScreen\(\s*onMangaClick = .*?,\s*onChapterClick = \{ chapterId -> navController\.navigate\("reader/\$chapterId"\) \}\s*\)', 
    'LibraryScreen(\n                                onMangaClick = { mangaId: String, mangaTitle: String, mangaCover: String -> \n                                    val intent = Intent(this@MainComposeActivity, MangaDetailActivity::class.java).apply {\n                                        putExtra("mangaUrl", mangaId)\n                                        putExtra("mangaTitle", mangaTitle)\n                                        putExtra("mangaCover", mangaCover)\n                                    }\n                                    startActivity(intent)\n                                },\n                                ' + correct_lambda + '\n                            )', content, flags=re.DOTALL)


# Let's fix DownloadsScreen
content = re.sub(r'DownloadsScreen\(\s*onChapterClick = \{ chapterId -> navController\.navigate\("reader/\$chapterId"\) \}\s*\)', 
    'DownloadsScreen(\n                                ' + correct_lambda + '\n                            )', content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
