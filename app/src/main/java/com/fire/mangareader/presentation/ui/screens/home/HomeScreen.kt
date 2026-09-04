package com.fire.mangareader.presentation.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fire.mangareader.presentation.ui.components.shimmerEffect
import androidx.compose.foundation.lazy.LazyRow
import coil.compose.AsyncImage

data class UIManga(val id: String, val title: String, val coverUrl: String, val latestChapter: String, val rating: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMangaClick: (String, String, String) -> Unit,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val heroMangas by viewModel.heroMangas.collectAsState()
    val recentUpdates by viewModel.recentMangas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manga Fire", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isLoading) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(12) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.7f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.height(14.dp).fillMaxWidth(0.8f).shimmerEffect())
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.height(10.dp).fillMaxWidth(0.5f).shimmerEffect())
                        }
                    }
                }

} else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                        Text(error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        val context = androidx.compose.ui.platform.LocalContext.current
                        Button(onClick = { viewModel.fetchData() }) {
                            Text("إعادة المحاولة")
                        }
                        if (error!!.contains("403") || error!!.contains("Cloudflare")) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                com.fire.mangareader.data.network.CloudflareBypassDialog(context, com.fire.mangareader.data.network.MangaScraper.BASE_URL, object : com.fire.mangareader.data.network.CloudflareBypassDialog.BypassCallback {
                                    override fun onSuccess(cookies: String?, userAgent: String?) {
                                        viewModel.fetchData()
                                    }
                                    override fun onFailed() {}
                                }).show()
                            }) {
                                Text("تخطي حماية Cloudflare")
                            }
                        }
                    }

                }
            } else {
                
                if (heroMangas.isNotEmpty()) {
                    HeroBanner(heroMangas, onMangaClick)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Continue Reading (Recent)
                Text(
                    "متابعة القراءة", 
                    style = MaterialTheme.typography.titleLarge, 
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(3) { index -> // Mocking 3 recent items
                        Row(
                            modifier = Modifier
                                .width(280.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = "https://mangalik.net/uploads/manga/cover/manga-mock/cover_250x350.jpg",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(80.dp)
                            )
                            Column(modifier = Modifier.padding(8.dp).weight(1f)) {
                                Text("سولو ليفيلينج", style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("الفصل ${105 + index}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { 0.7f },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                
                Text(
                    "أحدث التحديثات", 
                    style = MaterialTheme.typography.titleLarge, 
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(recentUpdates) { manga ->
                        MangaCard(manga, onMangaClick)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroBanner(mangas: List<UIManga>, onMangaClick: (String, String, String) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { mangas.size })
    
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) { page ->
        val manga = mangas[page]
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onMangaClick(manga.id, manga.title, manga.coverUrl) }
        ) {
            AsyncImage(
                model = manga.coverUrl,
                contentDescription = manga.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 100f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(manga.title, style = MaterialTheme.typography.titleLarge.copy(color = Color.White))
                Text(manga.latestChapter, style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray))
            }
        }
    }
}

@Composable
fun MangaCard(manga: UIManga, onMangaClick: (String, String, String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMangaClick(manga.id, manga.title, manga.coverUrl) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = manga.coverUrl,
                contentDescription = manga.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(bottomEnd = 8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(manga.rating, color = Color.White, style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            manga.title, 
            style = MaterialTheme.typography.bodyMedium, 
            maxLines = 1, 
            overflow = TextOverflow.Ellipsis
        )
        Text(
            manga.latestChapter, 
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        )
    }
}
