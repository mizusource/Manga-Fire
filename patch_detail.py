import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    text = f.read()

# I will replace the LazyColumn content entirely to use the new sections
# and add the new Composables at the bottom.

# 1. New composables
new_composables = """
@Composable
fun SectionMangaInfo(title: String, status: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Text(status, style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary))
        
        // Mock Genres
        androidx.compose.foundation.layout.FlowRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("أكشن", "خيال", "شونين", "مغامرة").forEach { genre ->
                androidx.compose.material3.SuggestionChip(
                    onClick = { },
                    label = { Text(genre) }
                )
            }
        }
    }
}

@Composable
fun SectionRating() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text("4.8", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold))
            Row {
                repeat(5) {
                    Icon(androidx.compose.material.icons.Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                }
            }
            Text("15,243 تقييم", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        
        Column(modifier = Modifier.weight(2f)) {
            listOf(5 to 0.8f, 4 to 0.15f, 3 to 0.03f, 2 to 0.01f, 1 to 0.01f).forEach { (star, progress) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$star", modifier = Modifier.padding(end = 4.dp), fontSize = 12.sp)
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp)),
                        color = Color(0xFFFFC107),
                        trackColor = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTrailer() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("العرض الدعائي", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = "https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg", // Mock thumbnail
                contentDescription = "Trailer",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().alpha(0.6f)
            )
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.5f), shape = androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play Trailer", tint = Color.Black)
            }
        }
    }
}

@Composable
fun SectionRelatedManga() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text("أعمال مشابهة", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.foundation.lazy.LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(5) {
                Column(modifier = Modifier.width(120.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .background(Color.Gray)
                    ) {
                        AsyncImage(
                            model = "https://mangalik.net/uploads/manga/cover/manga-mock/cover_250x350.jpg",
                            contentDescription = "Related",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Text(
                        "مانهوا مشابهة ${it+1}", 
                        maxLines = 1, 
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, 
                        modifier = Modifier.padding(top = 4.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
"""

text = text.replace("import androidx.compose.foundation.layout.*", "import androidx.compose.foundation.layout.*\nimport androidx.compose.ui.draw.clip\nimport androidx.compose.ui.draw.alpha")
text += "\n" + new_composables

# Update LazyColumn inside MangaDetailScreen
lazy_column_pattern = r'LazyColumn\(\s*modifier = Modifier\.fillMaxSize\(\)\s*\)\s*\{.*?(?=item \{\s*Text\(\s*text = "الفصول)'

new_lazy_column = """LazyColumn(
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
                    }
                }
                
                item {
                    SectionMangaInfo(title = title, status = status)
                }
                
                item {
                    SectionRating()
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
                    SectionTrailer()
                }
                
                item {
                    SectionRelatedManga()
                }
                
                """

text = re.sub(lazy_column_pattern, new_lazy_column, text, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(text)
