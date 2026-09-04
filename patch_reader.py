import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/reader/ChapterReaderScreen.kt", "r") as f:
    text = f.read()

# Add imports for the bottom sheet and sliders
imports_to_add = """
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.graphics.BlendMode
"""
text = text.replace("import androidx.compose.ui.graphics.Color", "import androidx.compose.ui.graphics.Color\n" + imports_to_add)


settings_sheet = """
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    onDismiss: () -> Unit,
    readingMode: String,
    onReadingModeChange: (String) -> Unit,
    keepScreenOn: Boolean,
    onKeepScreenOnChange: (Boolean) -> Unit,
    filterMode: Int,
    onFilterModeChange: (Int) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text("إعدادات القراءة", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Reading direction
            Text("اتجاه القراءة", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                Button(onClick = { onReadingModeChange("webtoon") }, colors = ButtonDefaults.buttonColors(containerColor = if (readingMode == "webtoon") MaterialTheme.colorScheme.primary else Color.Gray)) {
                    Text("عمودي (Webtoon)")
                }
                Button(onClick = { onReadingModeChange("pager_rtl") }, colors = ButtonDefaults.buttonColors(containerColor = if (readingMode == "pager_rtl") MaterialTheme.colorScheme.primary else Color.Gray)) {
                    Text("أفقي (RTL)")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Keep screen on
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("إبقاء الشاشة مضاءة", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = keepScreenOn, onCheckedChange = onKeepScreenOnChange)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Color Filter (Eye protection)
            Text("فلتر الألوان (حماية العين)", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                val filters = listOf("بدون", "تظليل", "دافيء", "قراءة ليلية")
                filters.forEachIndexed { index, name ->
                    androidx.compose.material3.FilterChip(
                        selected = filterMode == index,
                        onClick = { onFilterModeChange(index) },
                        label = { Text(name) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
"""

text = text + "\n" + settings_sheet

# Let's write this back to the file first, then we inject the logic in ChapterReaderScreen
with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/reader/ChapterReaderScreen.kt", "w") as f:
    f.write(text)
