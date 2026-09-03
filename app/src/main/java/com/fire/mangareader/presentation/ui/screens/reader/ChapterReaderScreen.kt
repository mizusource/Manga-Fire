package com.fire.mangareader.presentation.ui.screens.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterReaderScreen(
    chapterId: String,
    onBackClick: () -> Unit,
    viewModel: ReaderViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(chapterId) {
        viewModel.fetchPages(chapterId)
    }

    val pages by viewModel.pages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var isControlsVisible by remember { mutableStateOf(true) }

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
                    val context = androidx.compose.ui.platform.LocalContext.current
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
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .clickable { isControlsVisible = !isControlsVisible }
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
        }

        if (isControlsVisible) {
            TopAppBar(
                title = { Text(chapterId.replace("-", " ").capitalize(), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                ),
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
