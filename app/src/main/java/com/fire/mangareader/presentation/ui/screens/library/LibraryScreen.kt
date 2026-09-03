package com.fire.mangareader.presentation.ui.screens.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.fire.mangareader.data.local.entity.RecentManga
import com.fire.mangareader.presentation.ui.screens.home.MangaCard
import com.fire.mangareader.presentation.ui.screens.home.UIManga
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
    onMangaClick: (String, String, String) -> Unit,
    onChapterClick: (String, String, String, String, String) -> Unit,
    viewModel: LibraryViewModel = viewModel()
) {
    val favorites by viewModel.favorites.collectAsState()
    val history by viewModel.history.collectAsState()

    val tabs = listOf("المفضلة", "سجل القراءة")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            when (page) {
                0 -> FavoritesTab(
                        favorites = favorites, 
                        onMangaClick = onMangaClick, 
                        onDeleteClick = { viewModel.removeFavorite(it) }
                     )
                1 -> HistoryTab(
                        history = history, 
                        onMangaClick = onMangaClick, 
                        onChapterClick = onChapterClick,
                        onDeleteClick = { viewModel.removeHistory(it) }
                     )
            }
        }
    }
}

@Composable
fun FavoritesTab(
    favorites: List<UIManga>, 
    onMangaClick: (String, String, String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    if (favorites.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا توجد مانجا في المفضلة")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(favorites) { manga ->
                Box {
                    MangaCard(manga, onMangaClick)
                    IconButton(
                        onClick = { onDeleteClick(manga.id) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryTab(
    history: List<RecentManga>, 
    onMangaClick: (String, String, String) -> Unit, 
    onChapterClick: (String, String, String, String, String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    if (history.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لم تقرأ أي مانجا بعد")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(history) { manga ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onMangaClick(manga.id, manga.title, manga.coverUrl) },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Box {
                        Column {
                            AsyncImage(
                                model = manga.coverUrl,
                                contentDescription = manga.title,
                                modifier = Modifier.fillMaxWidth().aspectRatio(0.7f),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = manga.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "مواصلة: " + manga.lastReadChapterName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable { onChapterClick(manga.lastReadChapterId, manga.id, manga.lastReadChapterName, manga.title, manga.coverUrl) }
                                )
                            }
                        }
                        
                        IconButton(
                            onClick = { onDeleteClick(manga.id) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
