package com.fire.mangareader.presentation.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    onApply: (SearchFilter) -> Unit,
    initialFilter: SearchFilter
) {
    var currentFilter by remember { mutableStateOf(initialFilter.copy()) }

    val statuses = listOf("مستمر" to "ongoing", "مكتمل" to "completed", "متوقف" to "hiatus")
    val types = listOf("مانجا" to "manga", "مانهوا" to "manhwa", "مانهوا صينية" to "manhua")
    val genresList = listOf("أكشن", "مغامرة", "كوميدي", "دراما", "خيال", "تاريخي", "رعب", "رومانسي", "خيال علمي")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("خيارات التصفية", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("الحالة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statuses.forEach { (label, value) ->
                    FilterChip(
                        selected = currentFilter.status == value,
                        onClick = { currentFilter = currentFilter.copy(status = if (currentFilter.status == value) "" else value) },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text("نوع", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                types.forEach { (label, value) ->
                    FilterChip(
                        selected = currentFilter.type == value,
                        onClick = { currentFilter = currentFilter.copy(type = if (currentFilter.type == value) "" else value) },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("سنة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            var yearRange by remember { mutableStateOf(1975f..2025f) }
            RangeSlider(
                value = yearRange,
                onValueChange = { yearRange = it },
                valueRange = 1975f..2025f,
                steps = 50
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(yearRange.start.toInt().toString())
                Text(yearRange.endInclusive.toInt().toString())
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("عدد الفصول", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            var chapterRange by remember { mutableStateOf(0f..3000f) }
            RangeSlider(
                value = chapterRange,
                onValueChange = { chapterRange = it },
                valueRange = 0f..3000f,
                steps = 300
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(chapterRange.start.toInt().toString())
                Text(chapterRange.endInclusive.toInt().toString())
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("تصنيف", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genresList.forEach { genre ->
                    val isSelected = currentFilter.genres.contains(genre)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newGenres = if (isSelected) {
                                currentFilter.genres - genre
                            } else {
                                currentFilter.genres + genre
                            }
                            currentFilter = currentFilter.copy(genres = newGenres)
                        },
                        label = { Text(genre) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { 
                    onApply(currentFilter)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تطبيق الفلتر")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
