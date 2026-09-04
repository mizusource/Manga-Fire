import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/home/HomeScreen.kt", "r") as f:
    text = f.read()

# Add import
text = text.replace("import androidx.compose.ui.unit.dp", "import androidx.compose.ui.unit.dp\nimport com.fire.mangareader.presentation.ui.components.shimmerEffect\nimport androidx.compose.foundation.lazy.LazyRow")

# Replace loading state with shimmer
shimmer_grid = """
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(12) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.7f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.height(14.dp).fillMaxWidth(0.8f).shimmerEffect())
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.height(10.dp).fillMaxWidth(0.5f).shimmerEffect())
                        }
                    }
                }
"""

text = re.sub(
    r'if \(isLoading\) \{\s*Box\(modifier = Modifier\.fillMaxSize\(\), contentAlignment = Alignment\.Center\) \{\s*CircularProgressIndicator\(\)\s*\}\s*\} else',
    f'if (isLoading) {{\n{shimmer_grid}\n}} else',
    text,
    flags=re.DOTALL
)

# Add Recent Reading Section inside the else block (where the content is loaded)
recent_reading_code = """
                if (heroMangas.isNotEmpty()) {
                    HeroBanner(heroMangas, onMangaClick)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Continue Reading (Recent)
                Text(
                    "متابعة القراءة", 
                    style = MaterialTheme.typography.titleLarge, 
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(3) { index -> // Mocking 3 recent items
                        Row(
                            modifier = Modifier
                                .width(280.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = "https://mangalik.net/uploads/manga/cover/manga-mock/cover_250x350.jpg",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(80.dp)
                            )
                            Column(modifier = Modifier.padding(8.dp).weight(1f)) {
                                Text("سولو ليفيلينج", style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("الفصل ${105 + index}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { 0.7f },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
"""

text = re.sub(
    r'if \(heroMangas\.isNotEmpty\(\)\) \{\s*HeroBanner\(heroMangas, onMangaClick\)\s*Spacer\(modifier = Modifier\.height\(16\.dp\)\)\s*\}',
    recent_reading_code,
    text,
    flags=re.DOTALL
)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/home/HomeScreen.kt", "w") as f:
    f.write(text)
