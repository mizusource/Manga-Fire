with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

replacement = """
import com.fire.mangareader.presentation.ui.screens.downloads.DownloadsScreen

class MainComposeActivity : ComponentActivity() {
"""

content = content.replace("class MainComposeActivity : ComponentActivity() {", replacement)

nav_host_replacement = """
                        composable("library") { 
                            LibraryScreen(
                                onMangaClick = { mangaId -> navController.navigate("detail/$mangaId") }, 
                                onChapterClick = { chapterId -> navController.navigate("reader/$chapterId") }
                            ) 
                        }
                        composable("downloads") {
                            DownloadsScreen(
                                onChapterClick = { chapterId -> navController.navigate("reader/$chapterId") }
                            )
                        }
                        composable("settings") { 
                            ProfileScreen(
                                onDownloadsClick = { navController.navigate("downloads") }
                            ) 
                        }
"""

import re
content = re.sub(r'composable\("library"\) \{.*?\}.*?composable\("settings"\) \{ ProfileScreen\(\) \}', nav_host_replacement.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
