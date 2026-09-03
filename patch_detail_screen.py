import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

new_detail_screen = """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailScreen(
    mangaId: String,
    onBackClick: () -> Unit,
    onChapterClick: (String) -> Unit,
    viewModel: DetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(mangaId) {
        viewModel.fetchDetails(mangaId)
    }

    val title by viewModel.mangaTitle.collectAsState()
    val description by viewModel.description.collectAsState()
    val status by viewModel.status.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // نفترض أن الغلاف يتبع هذا النمط في موقع مانجا ليك غالباً
    val coverUrl = "https://mangalik.net/uploads/manga/cover/$mangaId/cover_250x350.jpg"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.fetchDetails(mangaId) }) {
                        Text("إعادة المحاولة")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                    ) {
                        AsyncImage(
                            model = coverUrl,
                            contentDescription = "Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                                        startY = 150f
                                    )
                                )
                        )
                        
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(title, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                            Text(status, style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)))
                        }
                    }
                }
                
                item {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
                
                item {
                    Text(
                        text = "الفصول (${chapters.size})",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                
                items(chapters) { chapter ->
                    // نأخذ الجزء الأخير من الرابط فقط ليكون الـ ID
                    val chapterId = chapter.url?.trimEnd('/')?.split("/")?.lastOrNull() ?: ""
                    ChapterItem(
                        title = chapter.title ?: "بدون عنوان", 
                        onClick = { onChapterClick(chapterId) },
                        onDownloadClick = {}
                    )
                }
            }
        }
    }
}"""

content = re.sub(r'@OptIn\(ExperimentalMaterial3Api::class\)\n@Composable\nfun MangaDetailScreen.*?\}\n}\n\n@Composable', new_detail_screen + '\n\n@Composable', content, flags=re.DOTALL)
content = content.replace('import androidx.compose.material.icons.filled.ArrowBack', 'import androidx.compose.material.icons.filled.ArrowBack\nimport androidx.compose.runtime.LaunchedEffect\nimport androidx.compose.runtime.collectAsState\nimport androidx.compose.runtime.getValue\nimport androidx.lifecycle.viewmodel.compose.viewModel')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched MangaDetailScreen")
