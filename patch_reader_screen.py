import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/ui/screens/reader/ChapterReaderScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

new_reader_screen = """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterReaderScreen(
    chapterId: String,
    onBackClick: () -> Unit,
    viewModel: ReaderViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(chapterId) {
        viewModel.fetchPages(chapterId)
    }

    val pages by viewModel.pages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var isControlsVisible by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.fetchPages(chapterId) }) {
                        Text("إعادة المحاولة")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .clickable { isControlsVisible = !isControlsVisible }
            ) {
                items(pages) { pageUrl ->
                    AsyncImage(
                        model = pageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (isControlsVisible) {
            TopAppBar(
                title = { Text(chapterId.replace("-", " ").capitalize(), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                ),
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}"""

content = re.sub(r'@OptIn\(ExperimentalMaterial3Api::class\)\n@Composable\nfun ChapterReaderScreen.*?\}\n}', new_reader_screen, content, flags=re.DOTALL)
content = content.replace('import androidx.compose.material.icons.filled.ArrowBack', 'import androidx.compose.material.icons.filled.ArrowBack\nimport androidx.compose.runtime.LaunchedEffect\nimport androidx.compose.runtime.collectAsState\nimport androidx.compose.runtime.getValue\nimport androidx.lifecycle.viewmodel.compose.viewModel')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched ChapterReaderScreen")
