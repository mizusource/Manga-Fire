import sys

content = """package com.fire.mangareader.presentation.ui.screens.reader

import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ChapterReaderScreen(
    chapterId: String,
    onBackClick: () -> Unit,
    viewModel: ReaderViewModel = viewModel()
) {
    LaunchedEffect(chapterId) {
        viewModel.fetchPages(chapterId)
    }

    val pages by viewModel.pages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var isControlsVisible by remember { mutableStateOf(true) }
    var readingMode by remember { mutableStateOf("webtoon") } // "webtoon" or "pager_rtl"

    val view = LocalView.current
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val window = activity?.window

    // إعدادات الـ System UI وإبقاء الشاشة مضاءة (تطبيق مزايا MangaSlayer)
    LaunchedEffect(isControlsVisible) {
        window?.let {
            val insetsController = WindowCompat.getInsetsController(it, view)
            if (isControlsVisible) {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
                it.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
                insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                it.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }
    
    // تنظيف الخصائص عند الخروج من القارئ
    DisposableEffect(Unit) {
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            window?.let {
                WindowCompat.getInsetsController(it, view).show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.fetchPages(chapterId) }) {
                        Text("إعادة المحاولة")
                    }
                    if (error!!.contains("403") || error!!.contains("Cloudflare")) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            val url = try {
                                String(android.util.Base64.decode(chapterId, android.util.Base64.URL_SAFE))
                            } catch (e: Exception) {
                                com.fire.mangareader.data.network.MangaScraper.BASE_URL
                            }
                            val safeUrl = if (url.startsWith("http")) url else com.fire.mangareader.data.network.MangaScraper.BASE_URL
                            com.fire.mangareader.data.network.CloudflareBypassDialog(context, safeUrl, object : com.fire.mangareader.data.network.CloudflareBypassDialog.BypassCallback {
                                override fun onSuccess(cookies: String?, userAgent: String?) {
                                    viewModel.fetchPages(chapterId)
                                }
                                override fun onFailed() {}
                            }).show()
                        }) {
                            Text("تخطي حماية Cloudflare")
                        }
                    }
                }
            }
        } else if (pages.isNotEmpty()) {
            val coroutineScope = rememberCoroutineScope()
            
            if (readingMode == "webtoon") {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { isControlsVisible = !isControlsVisible }
                            )
                        }
                ) {
                    items(pages) { pageUrl ->
                        SubcomposeAsyncImage(
                            model = pageUrl,
                            contentDescription = null,
                            loading = {
                                Box(modifier = Modifier.fillMaxWidth().height(400.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                }
                            },
                            error = {
                                Box(modifier = Modifier.fillMaxWidth().height(400.dp), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color.Red)
                                }
                            },
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })
                
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true // دعم التمرير من اليمين لليسار (RTL)
                ) { pageIndex ->
                    ZoomableImage(
                        imageUrl = pages[pageIndex],
                        onTap = { offset, width ->
                            // تقسيم الشاشة لـ 3 مناطق مثل MangaSlayer
                            if (offset.x < width * 0.3f) {
                                // النقر يساراً: الصفحة التالية (في الـ RTL تعني +1)
                                coroutineScope.launch {
                                    if (pagerState.currentPage < pages.size - 1) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            } else if (offset.x > width * 0.7f) {
                                // النقر يميناً: الصفحة السابقة
                                coroutineScope.launch {
                                    if (pagerState.currentPage > 0) {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            } else {
                                // النقر في المنتصف: إظهار/إخفاء شريط التحكم
                                isControlsVisible = !isControlsVisible
                            }
                        }
                    )
                }
                
                // مؤشر الصفحات
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (isControlsVisible) 100.dp else 24.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1} / ${pages.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        if (isControlsVisible) {
            TopAppBar(
                title = { Text(chapterId.replace("-", " ").capitalize(), color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.85f)
                ),
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // شريط الإعدادات السفلي
            Surface(
                color = Color.Black.copy(alpha = 0.85f),
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { readingMode = "webtoon" }) {
                        Icon(Icons.Default.List, contentDescription = "عمودي", tint = if (readingMode == "webtoon") MaterialTheme.colorScheme.primary else Color.White)
                    }
                    IconButton(onClick = { readingMode = "pager_rtl" }) {
                        Icon(Icons.Default.ViewDay, contentDescription = "أفقي", tint = if (readingMode == "pager_rtl") MaterialTheme.colorScheme.primary else Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ZoomableImage(imageUrl: String, onTap: (Offset, Float) -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        if (scale > 1f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = 2.5f
                            val maxX = (size.width * (scale - 1)) / 2
                            val maxY = (size.height * (scale - 1)) / 2
                            offset = Offset(
                                x = ((size.width / 2) - tapOffset.x) * scale,
                                y = ((size.height / 2) - tapOffset.y) * scale
                            ).run {
                                Offset(x.coerceIn(-maxX, maxX), y.coerceIn(-maxY, maxY))
                            }
                        }
                    },
                    onTap = { tapOffset ->
                        onTap(tapOffset, size.width.toFloat())
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(1f, 4f)
                    scale = newScale
                    
                    val maxX = (size.width * (scale - 1)) / 2
                    val maxY = (size.height * (scale - 1)) / 2
                    
                    offset = Offset(
                        x = (offset.x + pan.x * scale).coerceIn(-maxX, maxX),
                        y = (offset.y + pan.y * scale).coerceIn(-maxY, maxY)
                    )
                }
            }
    ) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = null,
            loading = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            },
            error = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color.Red)
                }
            },
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )
    }
}
"""

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/reader/ChapterReaderScreen.kt", "w") as f:
    f.write(content)
