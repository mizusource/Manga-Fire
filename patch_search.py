import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/search/SearchScreen.kt", "r") as f:
    content = f.read()

new_filter_sheet = """@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterSheetContent(
    filter: SearchFilter,
    onStatusChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onGenreToggle: (String) -> Unit,
    onClearFilters: () -> Unit,
    onYearRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onChapterRangeChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    val statuses = listOf("مستمرة" to "on-going", "مكتملة" to "end", "متوقفة" to "canceled", "في الانتظار" to "on-hold")
    val types = listOf("مانجا" to "manga", "مانهوا" to "manhwa", "مانها" to "manhua", "ويب تون" to "webtoon")
    val genres = listOf("أكشن" to "action", "مغامرات" to "adventure", "كوميدي" to "comedy", "دراما" to "drama",
                         "رومانسي" to "romance", "خيال" to "fantasy", "إيسيكاي" to "isekai", "رعب" to "horror",
                         "غموض" to "mystery", "نفسي" to "psychological", "حياة مدرسية" to "school-life", "رياضة" to "sports")

    Column(modifier = Modifier.padding(16.dp).fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars).verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("الفلاتر المتقدمة", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onClearFilters) {
                Text("مسح الكل")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("الحالة", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), 
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            statuses.forEach { (label, value) ->
                FilterChip(
                    selected = filter.status == value,
                    onClick = { onStatusChange(if (filter.status == value) "" else value) },
                    label = { Text(label) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("النوع", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), 
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            types.forEach { (label, value) ->
                FilterChip(
                    selected = filter.type == value,
                    onClick = { onTypeChange(if (filter.type == value) "" else value) },
                    label = { Text(label) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("سنة الإنتاج", style = MaterialTheme.typography.titleMedium)
        RangeSlider(
            value = filter.yearRange,
            onValueChange = onYearRangeChange,
            valueRange = 1975f..2025f,
            steps = 50
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${filter.yearRange.start.toInt()}")
            Text("${filter.yearRange.endInclusive.toInt()}")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("عدد الفصول", style = MaterialTheme.typography.titleMedium)
        RangeSlider(
            value = filter.chapterRange,
            onValueChange = onChapterRangeChange,
            valueRange = 0f..3000f,
            steps = 300
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${filter.chapterRange.start.toInt()}")
            Text("${filter.chapterRange.endInclusive.toInt()}")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("التصنيفات", style = MaterialTheme.typography.titleMedium)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            genres.forEach { (label, value) ->
                FilterChip(
                    selected = filter.genres.contains(value),
                    onClick = { onGenreToggle(value) },
                    label = { Text(label) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
"""

content = re.sub(r"@OptIn\(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class\)\s*@Composable\s*fun FilterSheetContent.*?\}\s*\}", new_filter_sheet, content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/search/SearchScreen.kt", "w") as f:
    f.write(content)
