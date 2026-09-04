package com.fire.mangareader.presentation.ui.screens.reader

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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.graphics.BlendMode

import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
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
    var readingMode by remember { mutableStateOf("webtoon") }
    var showSettings by remember { mutableStateOf(false) }
    var keepScreenOn by remember { mutableStateOf(true) }
    var filterMode by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
     // "webtoon" or "pager_rtl"

    val view = LocalView.current
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val window = activity?.window

    // إعدادات الـ System UI وإبقاء الشاشة مضاءة (تطبيق مزايا MangaSlayer)
    
    LaunchedEffect(isControlsVisible, keepScreenOn) {
        window?.let {
            val insetsController = WindowCompat.getInsetsController(it, view)
            if (isControlsVisible) {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            } else {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
                insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            if (keepScreenOn) {
                it.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                it.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(androidx.compose.material.icons.Icons.Default.Settings, contentDescription = "الإعدادات", tint = Color.White)
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
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    // Page slider
                    if (pages.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text("${pagerState.currentPage + 1}", color = Color.White)
                            Slider(
                                value = pagerState.currentPage.toFloat(),
                                onValueChange = { 
                                    // Update pager on drag
                                },
                                onValueChangeFinished = { 
                                    // coroutineScope.launch { pagerState.animateScrollToPage(page) }
                                },
                                valueRange = 0f..(pages.size - 1).coerceAtLeast(1).toFloat(),
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                                colors = androidx.compose.material3.SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("${pages.size}", color = Color.White)
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
        
        // Color Filter Overlay (حماية العين و الوضع الليلي)
        if (filterMode > 0) {
            val filterColor = when (filterMode) {
                1 -> Color.Black.copy(alpha = 0.4f) // تظليل
                2 -> Color(0x33FF9800) // دافيء
                3 -> Color(0x4D000000) // ليلي قوي
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(filterColor)
            )
        }
        
        // Settings Sheet
        if (showSettings) {
            ReaderSettingsSheet(
                onDismiss = { showSettings = false },
                readingMode = readingMode,
                onReadingModeChange = { readingMode = it },
                keepScreenOn = keepScreenOn,
                onKeepScreenOnChange = { keepScreenOn = it },
                filterMode = filterMode,
                onFilterModeChange = { filterMode = it }
            )
        }
} } @Composable
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    onDismiss: () -> Unit,
    readingMode: String,
    onReadingModeChange: (String) -> Unit,
    keepScreenOn: Boolean,
    onKeepScreenOnChange: (Boolean) -> Unit,
    filterMode: Int,
    onFilterModeChange: (Int) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text("إعدادات القراءة", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Reading direction
            Text("اتجاه القراءة", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                Button(onClick = { onReadingModeChange("webtoon") }, colors = ButtonDefaults.buttonColors(containerColor = if (readingMode == "webtoon") MaterialTheme.colorScheme.primary else Color.Gray)) {
                    Text("عمودي (Webtoon)")
                }
                Button(onClick = { onReadingModeChange("pager_rtl") }, colors = ButtonDefaults.buttonColors(containerColor = if (readingMode == "pager_rtl") MaterialTheme.colorScheme.primary else Color.Gray)) {
                    Text("أفقي (RTL)")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Keep screen on
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("إبقاء الشاشة مضاءة", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = keepScreenOn, onCheckedChange = onKeepScreenOnChange)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Color Filter (Eye protection)
            Text("فلتر الألوان (حماية العين)", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                val filters = listOf("بدون", "تظليل", "دافيء", "قراءة ليلية")
                filters.forEachIndexed { index, name ->
                    androidx.compose.material3.FilterChip(
                        selected = filterMode == index,
                        onClick = { onFilterModeChange(index) },
                        label = { Text(name) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
