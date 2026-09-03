with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    content = f.read()

replacement = """
import androidx.compose.ui.platform.LocalContext
import com.fire.mangareader.data.download.DownloadManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailScreen(
    mangaId: String,
    onBackClick: () -> Unit,
    onChapterClick: (String) -> Unit,
    viewModel: DetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val downloadManager = remember { DownloadManager(context) }
    val coroutineScope = rememberCoroutineScope()
"""

import re
content = re.sub(r"@OptIn\(ExperimentalMaterial3Api::class\)\s*@Composable\s*fun MangaDetailScreen\(\s*mangaId: String,\s*onBackClick: \(\) -> Unit,\s*onChapterClick: \(String\) -> Unit,\s*viewModel: DetailViewModel = androidx\.lifecycle\.viewmodel\.compose\.viewModel\(\)\s*\) \{", replacement.strip(), content, flags=re.DOTALL)

# Add import rememberCoroutineScope
if "import androidx.compose.runtime.rememberCoroutineScope" not in content:
    content = content.replace("import androidx.compose.runtime.remember", "import androidx.compose.runtime.remember\nimport androidx.compose.runtime.rememberCoroutineScope")


chapter_item_call = """
                    ChapterItem(
                        title = chapter.title ?: "بدون عنوان", 
                        onClick = { onChapterClick(chapterId) },
                        onDownloadClick = {
                            coroutineScope.launch {
                                downloadManager.enqueueDownload(
                                    chapterId = chapterId,
                                    mangaId = mangaId,
                                    mangaTitle = title,
                                    chapterTitle = chapter.title ?: "بدون عنوان"
                                )
                                android.widget.Toast.makeText(context, "تمت الإضافة لطابور التحميل", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
"""

content = re.sub(r"ChapterItem\(\s*title = chapter\.title \?: \"بدون عنوان\",\s*onClick = \{ onChapterClick\(chapterId\) \},\s*onDownloadClick = \{\}\s*\)", chapter_item_call.strip(), content)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(content)

