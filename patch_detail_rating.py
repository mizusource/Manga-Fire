with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    content = f.read()

import re

new_rating = """@Composable
fun SectionRating(
    onAddRatingClick: () -> Unit,
    onManageListClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Rating
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f).clickable { }
        ) {
            Icon(
                androidx.compose.material.icons.Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(28.dp).padding(bottom = 4.dp)
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text("8.70", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text("/10", style = MaterialTheme.typography.bodySmall)
            }
            Text("15.2K", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        
        // MAL Rating
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f).clickable { }
        ) {
            Icon(
                androidx.compose.material.icons.Icons.Default.Star, // Use standard star as fallback for MAL icon
                contentDescription = null,
                tint = Color(0xFF2E51A2), // MAL blue color
                modifier = Modifier.size(28.dp).padding(bottom = 4.dp)
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text("8.50", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text("/10", style = MaterialTheme.typography.bodySmall)
            }
            Text("120K", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        
        // Your Rating
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f).clickable { onAddRatingClick() }
        ) {
            Icon(
                androidx.compose.material.icons.Icons.Default.StarBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp).padding(bottom = 4.dp)
            )
            Text(
                "إضافة تقييم", 
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        // Manage List
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f).clickable { onManageListClick() }
        ) {
            Icon(
                androidx.compose.material.icons.Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp).padding(bottom = 4.dp)
            )
            Text(
                "إدارة", 
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}"""

old_rating_pattern = r"@Composable\s*fun SectionRating\(\)\s*\{.*?\}(?=\s*@Composable|\Z)"
content = re.sub(old_rating_pattern, new_rating, content, flags=re.DOTALL)

# Find where SectionRating is called and update it
content = content.replace("SectionRating()", "SectionRating(\n                        onAddRatingClick = { showRatingDialog = true },\n                        onManageListClick = { showListSheet = true }\n                    )")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(content)
