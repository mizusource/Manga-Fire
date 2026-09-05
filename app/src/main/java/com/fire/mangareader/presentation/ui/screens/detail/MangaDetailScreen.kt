package com.fire.mangareader.presentation.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.fire.mangareader.presentation.ui.screens.detail.RatingDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import androidx.compose.ui.platform.LocalContext
import com.fire.mangareader.data.download.DownloadManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailScreen(
    mangaId: String,
    onBackClick: () -> Unit,
    onChapterClick: (String) -> Unit,
    viewModel: DetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val downloadManager = remember { DownloadManager(context) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(mangaId) {
        viewModel.fetchDetails(mangaId)
    }

    val title by viewModel.mangaTitle.collectAsState()
    val description by viewModel.description.collectAsState()
    val status by viewModel.status.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val isFavorite by viewModel.isFavorite.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var showListSheet by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var selectedList by remember { mutableStateOf("reading") }
    

    // نفترض أن الغلاف يتبع هذا النمط في موقع مانجا ليك غالباً
    val coverUrl = "https://mangalik.net/uploads/manga/cover/$mangaId/cover_250x350.jpg"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showRatingDialog = true }) {
                        Icon(Icons.Default.Star, contentDescription = "تقييم", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(onClick = { viewModel.fetchDetails(mangaId) }) {
                        Text("إعادة المحاولة")
                    }
                    if (error!!.contains("403") || error!!.contains("Cloudflare")) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            val mangaUrl = try {
                                String(android.util.Base64.decode(mangaId, android.util.Base64.URL_SAFE))
                            } catch (e: Exception) {
                                com.fire.mangareader.data.network.MangaScraper.BASE_URL
                            }
                            val safeUrl = if (mangaUrl.startsWith("http")) mangaUrl else com.fire.mangareader.data.network.MangaScraper.BASE_URL
                            com.fire.mangareader.data.network.CloudflareBypassDialog(context, safeUrl, object : com.fire.mangareader.data.network.CloudflareBypassDialog.BypassCallback {
                                override fun onSuccess(cookies: String?, userAgent: String?) {
                                    viewModel.fetchDetails(mangaId)
                                }
                                override fun onFailed() {
                                    // Handle failure if needed
                                }
                            }).show()
                        }) {
                            Text("تخطي حماية Cloudflare")
                        }
                    }
                }

            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                    ) {
                        AsyncImage(
                            model = coverUrl,
                            contentDescription = "Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                                        startY = 150f
                                    )
                                )
                        )
                    }
                }
                
                item {
                    SectionMangaInfo(title = title, status = status)
                }
                
                item {
                    SectionRating()
                }
                
                item {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
                
                item {
                    SectionTrailer()
                }
                
                item {
                    SectionRelatedManga()
                }
                
                item {
                    Text(
                        text = "الفصول (${chapters.size})",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                
                items(chapters) { chapter ->
                    // نأخذ الجزء الأخير من الرابط فقط ليكون الـ ID
                    val chapterId = android.util.Base64.encodeToString((chapter.url ?: "").toByteArray(), android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP or android.util.Base64.NO_PADDING)
                    ChapterItem(
                        title = chapter.title ?: "بدون عنوان", 
                        onClick = { onChapterClick(chapterId) },
                        onDownloadClick = {
                            coroutineScope.launch {
                                downloadManager.enqueueDownload(
                                    chapterId,
                                    mangaId,
                                    title,
                                    chapter.title ?: "بدون عنوان"
                                )
                                android.widget.Toast.makeText(context, "تمت الإضافة لطابور التحميل", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
    
    
        if (showRatingDialog) {
            RatingDialog(
                onDismissRequest = { showRatingDialog = false },
                onRatingSubmit = { story, characters, art ->
                    // Handle rating submission
                    showRatingDialog = false
                }
            )
        }

        if (showListSheet) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showListSheet = false },
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text("إضافة إلى قائمتي", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(16.dp))
                
                val lists = listOf(
                    "reading" to "أقرأها حالياً",
                    "completed" to "مكتملة",
                    "plan_to_read" to "أرغب بقراءتها",
                    "dropped" to "متوقفة"
                )
                
                lists.forEach { (id, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                selectedList = id
                                viewModel.toggleFavorite(mangaId)
                                showListSheet = false
                                android.widget.Toast.makeText(context, "تم النقل إلى: $name", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = selectedList == id,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ChapterItem(title: String, onClick: () -> Unit, onDownloadClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
            Text("منذ يومين", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = onDownloadClick) {
                Icon(Icons.Default.Download, contentDescription = "Download")
            }
            IconButton(onClick = onClick) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Read")
            }
        }
    }
}


@Composable
fun SectionMangaInfo(title: String, status: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Text(status, style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary))
        
        // Mock Genres
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("أكشن", "خيال", "شونين", "مغامرة")) { genre ->
                androidx.compose.material3.SuggestionChip(
                    onClick = { },
                    label = { Text(genre) }
                )
            }
        }
    }
}

@Composable
fun SectionRating() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text("4.8", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold))
            Row {
                repeat(5) {
                    Icon(androidx.compose.material.icons.Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                }
            }
            Text("15,243 تقييم", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        
        Column(modifier = Modifier.weight(2f)) {
            listOf(5 to 0.8f, 4 to 0.15f, 3 to 0.03f, 2 to 0.01f, 1 to 0.01f).forEach { (star, progress) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$star", modifier = Modifier.padding(end = 4.dp), fontSize = 12.sp)
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp)),
                        color = Color(0xFFFFC107),
                        trackColor = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTrailer() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("العرض الدعائي", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = "https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg", // Mock thumbnail
                contentDescription = "Trailer",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().alpha(0.6f)
            )
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.5f), shape = androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play Trailer", tint = Color.Black)
            }
        }
    }
}

@Composable
fun SectionRelatedManga() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text("أعمال مشابهة", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.foundation.lazy.LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(5) {
                Column(modifier = Modifier.width(120.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .background(Color.Gray)
                    ) {
                        AsyncImage(
                            model = "https://mangalik.net/uploads/manga/cover/manga-mock/cover_250x350.jpg",
                            contentDescription = "Related",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Text(
                        "مانهوا مشابهة ${it+1}", 
                        maxLines = 1, 
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, 
                        modifier = Modifier.padding(top = 4.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
