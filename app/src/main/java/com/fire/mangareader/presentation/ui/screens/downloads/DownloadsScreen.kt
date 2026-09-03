package com.fire.mangareader.presentation.ui.screens.downloads

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fire.mangareader.data.local.entity.DownloadedChapter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    onChapterClick: (String, String, String, String, String) -> Unit,
    viewModel: DownloadsViewModel = viewModel()
) {
    val downloads by viewModel.downloads.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("قائمة التنزيلات", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        if (downloads.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    text = "لا توجد تنزيلات حالياً",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(downloads, key = { it.chapterId }) { download ->
                    DownloadItemRow(
                        download = download,
                        onClick = {
                            if (download.state == 2) {
                                onChapterClick(download.chapterId, download.mangaId, download.chapterTitle, download.mangaTitle, "")
                            }
                        },
                        onCancel = { viewModel.cancelDownload(download.chapterId) },
                        onDelete = { viewModel.deleteDownload(download.chapterId) }
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadItemRow(
    download: DownloadedChapter,
    onClick: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = download.state == 2, onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = download.mangaTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = download.chapterTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Status 
                when (download.state) {
                    0 -> Text("في الانتظار...", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                    1 -> {
                        val progressText = if (download.totalPages > 0) "${download.downloadedPages} / ${download.totalPages}" else "جاري التحميل..."
                        val progress = if (download.totalPages > 0) download.downloadedPages.toFloat() / download.totalPages else 0f
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.weight(1f).height(4.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(progressText, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    2 -> Text("مكتمل", color = Color(0xFF4CAF50), style = MaterialTheme.typography.labelMedium)
                    3 -> Text("حدث خطأ", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Actions
            when (download.state) {
                0, 1 -> {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء", tint = MaterialTheme.colorScheme.error)
                    }
                }
                2 -> {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                    }
                }
                3 -> {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
