import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/reader/ChapterReaderScreen.kt", "r") as f:
    text = f.read()

# Add states
state_pattern = r'var readingMode by remember \{ mutableStateOf\("webtoon"\) \}'
new_states = """
    var readingMode by remember { mutableStateOf("webtoon") }
    var showSettings by remember { mutableStateOf(false) }
    var keepScreenOn by remember { mutableStateOf(true) }
    var filterMode by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })
"""
text = re.sub(state_pattern, new_states.strip(), text)

# System UI patch
system_ui_pattern = r'LaunchedEffect\(isControlsVisible\) \{.*?(?=// تنظيف الخصائص عند الخروج من القارئ)'
new_system_ui = """
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
"""
text = re.sub(system_ui_pattern, new_system_ui, text, flags=re.DOTALL)

# Pager logic
# Remove the inner val pagerState = rememberPagerState... because we moved it up.
text = text.replace("val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })", "")


# Add settings icon to top bar
top_bar_pattern = r'TopAppBar\([\s\S]*?navigationIcon = \{[\s\S]*?\},'
new_top_bar = """
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
"""
text = re.sub(top_bar_pattern, new_top_bar.strip() + ",", text)

# Add Slider and SettingsSheet at the end of the Box
controls_pattern = r'// شريط الإعدادات السفلي[\s\S]*?(?=\}\s*\}\s*\}\s*@Composable)'
new_controls = """
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
"""
text = re.sub(controls_pattern, new_controls, text)

# Add Settings icon import
text = text.replace("import androidx.compose.material.icons.filled.Warning", "import androidx.compose.material.icons.filled.Warning\nimport androidx.compose.material.icons.filled.Settings")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/reader/ChapterReaderScreen.kt", "w") as f:
    f.write(text)
