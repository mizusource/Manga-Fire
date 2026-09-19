package com.fire.mangareader.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.fire.mangareader.model.Chapter
import com.fire.mangareader.model.MangaDetails
import com.fire.mangareader.network.NetworkClient
import com.fire.mangareader.ui.MangaViewModel
import com.fire.mangareader.ui.UiState
import com.fire.mangareader.ui.theme.DarkBackground
import com.fire.mangareader.ui.theme.DarkSurface
import com.fire.mangareader.ui.theme.PrimaryRed
import com.fire.mangareader.ui.theme.TextPrimary
import com.fire.mangareader.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    manga: MangaDetails,
    initialChapter: Chapter,
    viewModel: MangaViewModel,
    onBack: () -> Unit
) {
    val readerState by viewModel.readerState.collectAsState()
    val currentChapter by viewModel.currentChapter.collectAsState()
    val activeChapter = currentChapter ?: initialChapter

    var showControls by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()

    // Determine Prev and Next Chapters
    val currentIdx = manga.chapters.indexOfFirst { it.id == activeChapter.id }
    val nextChapter = if (currentIdx > 0) manga.chapters[currentIdx - 1] else null
    val prevChapter = if (currentIdx >= 0 && currentIdx < manga.chapters.size - 1) manga.chapters[currentIdx + 1] else null

    // Page index indicator
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = showControls,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = manga.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = activeChapter.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkSurface.copy(alpha = 0.95f)
                    )
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showControls,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurface.copy(alpha = 0.95f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Chapter Button
                    Button(
                        onClick = { prevChapter?.let { viewModel.loadChapterPages(it, manga) } },
                        enabled = prevChapter != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkBackground,
                            contentColor = TextPrimary,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = TextSecondary.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("prev_chapter_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "الفصل السابق")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("الفصل السابق", fontSize = 12.sp)
                    }

                    // Next Chapter Button
                    Button(
                        onClick = { nextChapter?.let { viewModel.loadChapterPages(it, manga) } },
                        enabled = nextChapter != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryRed,
                            contentColor = Color.White,
                            disabledContainerColor = DarkBackground,
                            disabledContentColor = TextSecondary.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("next_chapter_button")
                    ) {
                        Text("الفصل التالي", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "الفصل التالي")
                    }
                }
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showControls = !showControls
                }
        ) {
            when (val state = readerState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PrimaryRed)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "جاري تحميل صور ${activeChapter.name}...",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = state.message,
                                color = TextPrimary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            if (state.isCloudflare && state.blockedUrl != null) {
                                Button(
                                    onClick = { viewModel.triggerCloudflareBypass(state.blockedUrl) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                                    modifier = Modifier.testTag("solve_cloudflare_reader_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Security, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تخطي حماية Cloudflare الآن")
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.loadChapterPages(activeChapter, manga) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                                ) {
                                    Text("إعادة المحاولة")
                                }
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    val pages = state.data
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("reader_page_list")
                    ) {
                        itemsIndexed(pages, key = { _, page -> page.imageUrl }) { index, page ->
                            var scale by remember { mutableFloatStateOf(1f) }
                            val transformState = rememberTransformableState { zoomChange, _, _ ->
                                scale = (scale * zoomChange).coerceIn(1f, 3.5f)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformable(state = transformState)
                                    .graphicsLayer(scaleX = scale, scaleY = scale),
                                contentAlignment = Alignment.Center
                            ) {
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(page.imageUrl)
                                        .crossfade(true)
                                        .addHeader("User-Agent", NetworkClient.DEFAULT_USER_AGENT)
                                        .addHeader("Referer", page.referer)
                                        .build(),
                                    contentDescription = "صفحة ${index + 1}",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth(),
                                    loading = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(350.dp)
                                                .background(DarkBackground),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                color = PrimaryRed,
                                                modifier = Modifier.size(28.dp),
                                                strokeWidth = 2.dp
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        // Bottom End of Chapter Indicator
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "نهاية ${activeChapter.name}",
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                if (nextChapter != null) {
                                    Button(
                                        onClick = { viewModel.loadChapterPages(nextChapter, manga) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                                    ) {
                                        Text("الانتقال إلى ${nextChapter.name}")
                                    }
                                }
                            }
                        }
                    }

                    // Floating Page Indicator Badge
                    if (pages.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = if (showControls) 70.dp else 24.dp)
                                .background(Color.Black.copy(alpha = 0.75f), CircleShape)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${(firstVisibleItemIndex + 1).coerceAtMost(pages.size)} / ${pages.size}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                UiState.Idle -> {}
            }
        }
    }
}
