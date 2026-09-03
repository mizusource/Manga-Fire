import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/ui/screens/home/HomeScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

# Replace HomeScreen signature and body to use ViewModel
new_home_screen = """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMangaClick: (String) -> Unit,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val heroMangas by viewModel.heroMangas.collectAsState()
    val recentUpdates by viewModel.recentMangas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manga Fire", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchData() }) {
                            Text("إعادة المحاولة")
                        }
                    }
                }
            } else {
                if (heroMangas.isNotEmpty()) {
                    HeroBanner(heroMangas, onMangaClick)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Text(
                    "أحدث التحديثات", 
                    style = MaterialTheme.typography.titleLarge, 
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(recentUpdates) { manga ->
                        MangaCard(manga, onMangaClick)
                    }
                }
            }
        }
    }
}"""

content = re.sub(r'@OptIn\(ExperimentalMaterial3Api::class\)\n@Composable\nfun HomeScreen.*?\}\n}\n\n@OptIn', new_home_screen + '\n\n@OptIn', content, flags=re.DOTALL)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched HomeScreen")
