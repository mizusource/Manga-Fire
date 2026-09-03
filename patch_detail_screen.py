import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('import androidx.compose.material.icons.filled.ArrowBack',
                          'import androidx.compose.material.icons.filled.ArrowBack\nimport androidx.compose.material.icons.filled.Favorite\nimport androidx.compose.material.icons.filled.FavoriteBorder\nimport androidx.compose.ui.graphics.Color')

content = content.replace('val isLoading by viewModel.isLoading.collectAsState()',
                          'val isLoading by viewModel.isLoading.collectAsState()\n    val isFavorite by viewModel.isFavorite.collectAsState()')

content = content.replace('viewModel.fetchMangaDetails(mangaId)',
                          'viewModel.fetchMangaDetails(mangaId)\n        viewModel.checkFavoriteStatus(mangaId)')

content = content.replace('title = { Text(mangaDetails?.title ?: "تفاصيل المانجا", maxLines = 1, overflow = TextOverflow.Ellipsis) }',
                          'title = { Text(mangaDetails?.title ?: "تفاصيل المانجا", maxLines = 1, overflow = TextOverflow.Ellipsis) },\n                actions = {\n                    IconButton(onClick = { viewModel.toggleFavorite(mangaId) }) {\n                        Icon(\n                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,\n                            contentDescription = "Favorite",\n                            tint = if (isFavorite) Color.Red else LocalContentColor.current\n                        )\n                    }\n                }')

with open(filepath, 'w') as f:
    f.write(content)
