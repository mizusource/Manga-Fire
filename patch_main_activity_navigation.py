import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

# Replace HomeScreen Intent
home_intent = '''                                onMangaClick = { mangaId: String, mangaTitle: String, mangaCover: String -> 
                                    val intent = Intent(this@MainComposeActivity, MangaDetailActivity::class.java).apply {
                                        putExtra("mangaUrl", decodeUrl(mangaId))
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                }'''
home_nav = '''                                onMangaClick = { mangaId: String, mangaTitle: String, mangaCover: String -> 
                                    val safeId = java.net.URLEncoder.encode(mangaId, "UTF-8")
                                    navController.navigate("detail/$safeId")
                                }'''
content = content.replace(home_intent, home_nav)

# Replace SearchScreen Intent
search_intent = '''                                onMangaClick = { mangaId: String, mangaTitle: String, mangaCover: String -> 
                                    val intent = Intent(this@MainComposeActivity, MangaDetailActivity::class.java).apply {
                                        putExtra("mangaUrl", decodeUrl(mangaId))
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                }'''
search_nav = '''                                onMangaClick = { mangaId: String, mangaTitle: String, mangaCover: String -> 
                                    val safeId = java.net.URLEncoder.encode(mangaId, "UTF-8")
                                    navController.navigate("detail/$safeId")
                                }'''
content = content.replace(search_intent, search_nav)

# Replace LibraryScreen Intent
library_intent_manga = '''                                onMangaClick = { mangaId: String, mangaTitle: String, mangaCover: String -> 
                                    val intent = Intent(this@MainComposeActivity, MangaDetailActivity::class.java).apply {
                                        putExtra("mangaUrl", decodeUrl(mangaId))
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                }'''
library_nav_manga = '''                                onMangaClick = { mangaId: String, mangaTitle: String, mangaCover: String -> 
                                    val safeId = java.net.URLEncoder.encode(mangaId, "UTF-8")
                                    navController.navigate("detail/$safeId")
                                }'''
content = content.replace(library_intent_manga, library_nav_manga)

library_intent_chapter = '''                                onChapterClick = { chapterId: String, mangaId: String, chapterTitle: String, mangaTitle: String, mangaCover: String -> 
                                    val intent = Intent(this@MainComposeActivity, ChapterReaderActivity::class.java).apply {
                                        putExtra("chapterUrl", decodeUrl(chapterId))
                                        putExtra("mangaUrl", decodeUrl(mangaId))
                                        putExtra("chapterTitle", chapterTitle)
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                }'''
library_nav_chapter = '''                                onChapterClick = { chapterId: String, mangaId: String, chapterTitle: String, mangaTitle: String, mangaCover: String -> 
                                    val safeChapterId = java.net.URLEncoder.encode(chapterId, "UTF-8")
                                    navController.navigate("reader/$safeChapterId")
                                }'''
content = content.replace(library_intent_chapter, library_nav_chapter)

# Replace DownloadsScreen Intent
downloads_intent = '''                                onChapterClick = { chapterId: String, mangaId: String, chapterTitle: String, mangaTitle: String, mangaCover: String -> 
                                    val intent = Intent(this@MainComposeActivity, ChapterReaderActivity::class.java).apply {
                                        putExtra("chapterUrl", decodeUrl(chapterId))
                                        putExtra("mangaUrl", decodeUrl(mangaId))
                                        putExtra("chapterTitle", chapterTitle)
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                }'''
downloads_nav = '''                                onChapterClick = { chapterId: String, mangaId: String, chapterTitle: String, mangaTitle: String, mangaCover: String -> 
                                    val safeChapterId = java.net.URLEncoder.encode(chapterId, "UTF-8")
                                    navController.navigate("reader/$safeChapterId")
                                }'''
content = content.replace(downloads_intent, downloads_nav)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)

