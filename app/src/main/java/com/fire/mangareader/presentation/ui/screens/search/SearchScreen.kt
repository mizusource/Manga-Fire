package com.fire.mangareader.presentation.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.fire.mangareader.presentation.ui.screens.home.MangaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onMangaClick: (String, String, String) -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val filter by viewModel.filter.collectAsState()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    var showFilterSheet by remember { mutableStateOf(false) }

    val isFilterActive = filter.status.isNotEmpty() || filter.type.isNotEmpty() || filter.genres.isNotEmpty()

    Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
        OutlinedTextField(
            value = filter.query,
            onValueChange = { viewModel.onQueryChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("ابحث عن مانجا...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                Row {
                    if (filter.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "مسح")
                        }
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            Icons.Default.FilterList, 
                            contentDescription = "فلتر",
                            tint = if (isFilterActive) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.large
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (searchResults.loadState.refresh is LoadState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (searchResults.loadState.refresh is LoadState.Error) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "حدث خطأ أثناء البحث",
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else if (searchResults.itemCount == 0 && (filter.query.isNotEmpty() || isFilterActive)) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد نتائج مطابقة")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    count = searchResults.itemCount,
                    key = searchResults.itemKey { it.id },
                    contentType = searchResults.itemContentType { "Manga" }
                ) { index ->
                    val manga = searchResults[index]
                    if (manga != null) {
                        MangaCard(manga, onMangaClick)
                    }
                }

                if (searchResults.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            FilterSheetContent(
                filter = filter,
                onStatusChange = viewModel::onStatusChange,
                onTypeChange = viewModel::onTypeChange,
                onGenreToggle = viewModel::toggleGenre,
                onClearFilters = viewModel::clearFilters
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterSheetContent(
    filter: SearchFilter,
    onStatusChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onGenreToggle: (String) -> Unit,
    onClearFilters: () -> Unit
) {
    val statuses = listOf("مستمرة" to "on-going", "مكتملة" to "end", "متوقفة" to "canceled", "في الانتظار" to "on-hold")
    val types = listOf("مانجا" to "manga", "مانهوا" to "manhwa", "مانها" to "manhua", "ويب تون" to "webtoon")
    val genres = listOf("أكشن" to "action", "مغامرات" to "adventure", "كوميدي" to "comedy", "دراما" to "drama", 
                        "رومانسي" to "romance", "خيال" to "fantasy", "إيسيكاي" to "isekai", "رعب" to "horror", 
                        "غموض" to "mystery", "نفسي" to "psychological", "حياة مدرسية" to "school-life", "رياضة" to "sports")

    Column(modifier = Modifier.padding(16.dp).fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars)) {
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
                    onClick = { onStatusChange(value) },
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
                    onClick = { onTypeChange(value) },
                    label = { Text(label) }
                )
            }
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
