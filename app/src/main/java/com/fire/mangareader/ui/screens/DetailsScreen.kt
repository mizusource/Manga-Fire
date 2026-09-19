package com.fire.mangareader.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fire.mangareader.model.Chapter
import com.fire.mangareader.model.MangaDetails
import com.fire.mangareader.model.MangaItem
import com.fire.mangareader.network.NetworkClient
import com.fire.mangareader.ui.MangaViewModel
import com.fire.mangareader.ui.UiState
import com.fire.mangareader.ui.theme.BorderSubtle
import com.fire.mangareader.ui.theme.CardElevated
import com.fire.mangareader.ui.theme.DarkBackground
import com.fire.mangareader.ui.theme.DarkSurface
import com.fire.mangareader.ui.theme.DarkSurfaceVariant
import com.fire.mangareader.ui.theme.PrimaryRed
import com.fire.mangareader.ui.theme.SuccessGreen
import com.fire.mangareader.ui.theme.TextPrimary
import com.fire.mangareader.ui.theme.TextSecondary
import com.fire.mangareader.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(
    manga: MangaItem,
    viewModel: MangaViewModel,
    onBack: () -> Unit,
    onChapterClick: (Chapter, MangaDetails) -> Unit
) {
    val detailsState by viewModel.detailsState.collectAsState()
    val isFav = viewModel.isFavorite(manga.id)
    val history by viewModel.repository.history.collectAsState()
    val readingProgress = history[manga.id]

    var isAscending by remember { mutableStateOf(false) }
    var isDescExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = manga.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleFavorite(manga) },
                        modifier = Modifier.testTag("toggle_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "حفظ",
                            tint = if (isFav) PrimaryRed else TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = detailsState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryRed)
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(state.message, color = TextPrimary)
                            Spacer(modifier = Modifier.height(16.dp))
                            if (state.isCloudflare && state.blockedUrl != null) {
                                Button(
                                    onClick = { viewModel.triggerCloudflareBypass(state.blockedUrl) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                                    modifier = Modifier.testTag("solve_cloudflare_details_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Security, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تخطي فحص Cloudflare")
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.loadMangaDetails(manga) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                                ) {
                                    Text("إعادة المحاولة")
                                }
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    val details = state.data
                    val sortedChapters = remember(details.chapters, isAscending) {
                        if (isAscending) details.chapters.reversed() else details.chapters
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        // Hero Section
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                            ) {
                                // Blurred Backdrop
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(details.coverUrl)
                                        .crossfade(true)
                                        .addHeader("User-Agent", NetworkClient.DEFAULT_USER_AGENT)
                                        .addHeader("Referer", details.url)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Dark Overlay Gradient
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    DarkBackground.copy(alpha = 0.5f),
                                                    DarkBackground.copy(alpha = 0.85f),
                                                    DarkBackground
                                                )
                                            )
                                        )
                                )

                                // Content Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    // Cover Image
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = CardDefaults.cardElevation(8.dp),
                                        modifier = Modifier
                                            .width(105.dp)
                                            .aspectRatio(0.72f)
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(details.coverUrl)
                                                .crossfade(true)
                                                .addHeader("User-Agent", NetworkClient.DEFAULT_USER_AGENT)
                                                .addHeader("Referer", details.url)
                                                .build(),
                                            contentDescription = details.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    // Metadata Column
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = details.title,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        if (!details.author.isNullOrBlank()) {
                                            Text(
                                                text = "المؤلف: ${details.author}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }

                                        if (!details.status.isNullOrBlank()) {
                                            Text(
                                                text = "الحالة: ${details.status}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = PrimaryRed
                                            )
                                        }

                                        Text(
                                            text = "المصدر: ${details.sourceId.displayName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextTertiary
                                        )
                                    }
                                }
                            }
                        }

                        // Genres FlowRow
                        if (details.genres.isNotEmpty()) {
                            item {
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    details.genres.forEach { genre ->
                                        Box(
                                            modifier = Modifier
                                                .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = genre,
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Read / Resume Button
                        item {
                            val firstChapter = details.chapters.lastOrNull()
                            val resumeChapter = details.chapters.find { it.id == readingProgress?.chapterId } ?: firstChapter

                            if (resumeChapter != null) {
                                Button(
                                    onClick = { onChapterClick(resumeChapter, details) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .testTag("start_reading_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (readingProgress != null) "متابعة القراءة: ${readingProgress.chapterName}" else "ابدأ القراءة: ${resumeChapter.name}",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Synopsis / Description
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .background(DarkSurface, RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                                    .clickable { isDescExpanded = !isDescExpanded }
                                    .animateContentSize()
                            ) {
                                Text(
                                    text = "القصة",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = details.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = if (isDescExpanded) Int.MAX_VALUE else 3,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 20.sp
                                )
                                Text(
                                    text = if (isDescExpanded) "عرض أقل" else "عرض المزيد...",
                                    color = PrimaryRed,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        // Chapters Header
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "الفصول (${details.chapters.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                IconButton(onClick = { isAscending = !isAscending }) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.SwapVert,
                                            contentDescription = "ترتيب",
                                            tint = TextSecondary
                                        )
                                        Text(
                                            text = if (isAscending) "تصاعدي" else "تنازلي",
                                            color = TextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Chapters List
                        items(sortedChapters, key = { it.id }) { chapter ->
                            val isRead = readingProgress?.chapterId == chapter.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onChapterClick(chapter, details) }
                                    .testTag("chapter_item_${chapter.id}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isRead) DarkSurfaceVariant else CardElevated
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (isRead) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "تمت القراءة",
                                                tint = SuccessGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        Text(
                                            text = chapter.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isRead) FontWeight.Normal else FontWeight.SemiBold,
                                            color = if (isRead) TextSecondary else TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    if (!chapter.date.isNullOrBlank()) {
                                        Text(
                                            text = chapter.date,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextTertiary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                UiState.Idle -> {}
            }
        }
    }
}
