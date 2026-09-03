import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('import com.fire.mangareader.presentation.ui.screens.search.SearchScreen\nimport com.fire.mangareader.presentation.ui.screens.reader.ChapterReaderScreen',
                          'import com.fire.mangareader.presentation.ui.screens.search.SearchScreen\nimport com.fire.mangareader.presentation.ui.screens.reader.ChapterReaderScreen\nimport com.fire.mangareader.presentation.ui.screens.library.LibraryScreen')

content = content.replace('composable("library") { LibraryScreen() }',
                          'composable("library") { LibraryScreen(onMangaClick = { mangaId -> navController.navigate("detail/$mangaId") }, onChapterClick = { chapterId -> navController.navigate("reader/$chapterId") }) }')

content = re.sub(r'@Composable\nfun LibraryScreen\(\).*?\}\n', '', content, flags=re.DOTALL)

with open(filepath, 'w') as f:
    f.write(content)
