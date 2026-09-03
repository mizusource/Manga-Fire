with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/reader/ChapterReaderScreen.kt", "r") as f:
    content = f.read()

imports_to_add = """
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.foundation.shape.RoundedCornerShape
"""

content = content.replace("import coil.compose.AsyncImage", "import coil.compose.AsyncImage\n" + imports_to_add)

# Find the Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
# We need to add system ui toggling

sys_ui_code = """
    val view = LocalView.current
    val window = (view.context as? android.app.Activity)?.window
    
    LaunchedEffect(isControlsVisible) {
        window?.let {
            val insetsController = WindowCompat.getInsetsController(it, view)
            if (isControlsVisible) {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            } else {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
                insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    var readingMode by remember { mutableStateOf("webtoon") } // "webtoon" or "pager_rtl"

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
"""
content = content.replace("Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {", sys_ui_code)

# Replace the LazyColumn part with a switch between Webtoon and Pager
reader_code = """
            if (readingMode == "webtoon") {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null
                        ) { isControlsVisible = !isControlsVisible }
                ) {
                    items(pages) { pageUrl ->
                        AsyncImage(
                            model = pageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                val pagerState = rememberPagerState(initialPage = pages.size - 1, pageCount = { pages.size })
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                        .clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null
                        ) { isControlsVisible = !isControlsVisible },
                    reverseLayout = true
                ) { pageIndex ->
                    ZoomableImage(imageUrl = pages[pageIndex])
                }
                
                // Page Indicator for Pager Mode
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
"""
import re
# We need to replace the LazyColumn part
pattern = re.compile(r"LazyColumn\(\s*modifier = Modifier\.fillMaxSize\(\)\s*\.clickable \{ isControlsVisible = !isControlsVisible \}\s*\) \{\s*items\(pages\) \{ pageUrl ->\s*AsyncImage\(\s*model = pageUrl,\s*contentDescription = null,\s*contentScale = ContentScale\.FillWidth,\s*modifier = Modifier\.fillMaxWidth\(\)\s*\)\s*\}\s*\}", re.DOTALL)

content = pattern.sub(reader_code.strip(), content)

# Bottom Bar code
bottom_bar_code = """
        if (isControlsVisible) {
            TopAppBar(
                title = { Text(chapterId.replace("-", " ").capitalize(), color = Color.White, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) },
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

            // Bottom Settings Bar
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
fun ZoomableImage(imageUrl: String) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(1f, 3f)
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
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
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

pattern2 = re.compile(r"if \(isControlsVisible\) \{\s*TopAppBar\([^)]+\)\s*\}", re.DOTALL)
content = content.replace("if (isControlsVisible) {\n            TopAppBar(\n                title = { Text(chapterId.replace(\"-\", \" \").capitalize(), color = Color.White) },\n                navigationIcon = {\n                    IconButton(onClick = onBackClick) {\n                        Icon(Icons.Default.ArrowBack, contentDescription = \"رجوع\", tint = Color.White)\n                    }\n                },\n                colors = TopAppBarDefaults.topAppBarColors(\n                    containerColor = Color.Black.copy(alpha = 0.7f)\n                ),\n                modifier = Modifier.align(Alignment.TopCenter)\n            )\n        }\n    }\n}", bottom_bar_code.strip())

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/reader/ChapterReaderScreen.kt", "w") as f:
    f.write(content)
