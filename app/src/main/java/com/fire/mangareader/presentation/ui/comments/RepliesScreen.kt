package com.fire.mangareader.presentation.ui.comments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fire.mangareader.data.local.CommentEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepliesScreen(
    parentComment: CommentEntity?,
    replies: List<CommentEntity>,
    onBackClick: () -> Unit,
    onAddReply: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الردود") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            CommentInputBar(onSend = { content, _ -> 
                onAddReply(content)
            })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (parentComment != null) {
                item {
                    Text("التعليق الأصلي", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    CommentItem(
                        comment = parentComment,
                        onLike = {},
                        onDislike = {},
                        onDelete = {}
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                }
            }

            if (replies.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("لا توجد ردود بعد.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(replies, key = { it.id }) { reply ->
                    CommentItem(
                        comment = reply,
                        onLike = {},
                        onDislike = {},
                        onDelete = {}
                    )
                }
            }
        }
    }
}
