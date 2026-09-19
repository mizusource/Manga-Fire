package com.fire.mangareader.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fire.mangareader.model.MangaItem
import com.fire.mangareader.model.MangaSourceId
import com.fire.mangareader.ui.MangaViewModel
import com.fire.mangareader.ui.UiState
import com.fire.mangareader.ui.components.MangaCard
import com.fire.mangareader.ui.theme.BorderSubtle
import com.fire.mangareader.ui.theme.CardElevated
import com.fire.mangareader.ui.theme.DarkBackground
import com.fire.mangareader.ui.theme.DarkSurface
import com.fire.mangareader.ui.theme.DarkSurfaceVariant
import com.fire.mangareader.ui.theme.PrimaryRed
import com.fire.mangareader.ui.theme.TextPrimary
import com.fire.mangareader.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MangaViewModel,
    onMangaClick: (MangaItem) -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val selectedSource by viewModel.selectedSource.collectAsState()
    val mangaListState by viewModel.mangaListState.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val favorites by viewModel.repository.favorites.collectAsState()

    var selectedOrder by remember { mutableStateOf("latest") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(PrimaryRed, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "M",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Manga Community",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "تصفح واقرأ أحدث الفصول",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToFavorites,
                        modifier = Modifier.testTag("favorites_nav_button")
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "المفضلة",
                                tint = if (favorites.isNotEmpty()) PrimaryRed else TextSecondary
                            )
                            if (favorites.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(PrimaryRed, CircleShape)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Source selector row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MangaSourceId.values().forEach { source ->
                    val isSelected = source == selectedSource
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) PrimaryRed else DarkSurfaceVariant)
                            .clickable { viewModel.setSource(source) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("source_tab_${source.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = source.displayName,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("search_input"),
                placeholder = {
                    Text(
                        text = "ابحث عن مانجا في ${selectedSource.displayName}...",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = TextSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "مسح",
                                tint = TextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    viewModel.performSearch(searchQuery)
                })
            )

            // Sort filter chips (if not searching)
            AnimatedVisibility(visible = searchQuery.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val orders = listOf(
                        "latest" to "الأحدث",
                        "views" to "الأكثر مشاهدة",
                        "rating" to "الأعلى تقييماً"
                    )
                    orders.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedOrder == key,
                            onClick = {
                                selectedOrder = key
                                viewModel.loadMangaList(key)
                            },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryRed.copy(alpha = 0.2f),
                                selectedLabelColor = PrimaryRed,
                                containerColor = DarkSurfaceVariant,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedOrder == key,
                                borderColor = BorderSubtle,
                                selectedBorderColor = PrimaryRed
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main Content Area
            val activeState = if (searchQuery.isNotBlank() && searchState !is UiState.Idle) {
                searchState
            } else {
                mangaListState
            }

            when (val state = activeState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("loading_spinner"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PrimaryRed)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "جاري جلب الفصول من ${selectedSource.displayName}...",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = if (state.isCloudflare) Icons.Default.Security else Icons.Default.Refresh,
                                contentDescription = null,
                                tint = PrimaryRed,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = state.message,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (state.isCloudflare && state.blockedUrl != null) {
                                Button(
                                    onClick = { viewModel.triggerCloudflareBypass(state.blockedUrl) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("solve_cloudflare_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Security, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تخطي حماية Cloudflare الآن")
                                }
                            } else {
                                Button(
                                    onClick = {
                                        if (searchQuery.isNotBlank()) {
                                            viewModel.performSearch(searchQuery)
                                        } else {
                                            viewModel.loadMangaList(selectedOrder)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("retry_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("إعادة المحاولة")
                                }
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    val items = state.data
                    if (items.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("لا توجد مانجا متاحة حالياً.", color = TextSecondary)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("manga_grid")
                        ) {
                            items(items, key = { it.id }) { manga ->
                                MangaCard(
                                    manga = manga,
                                    onClick = { onMangaClick(manga) }
                                )
                            }
                        }
                    }
                }
                UiState.Idle -> {}
            }
        }
    }
}
