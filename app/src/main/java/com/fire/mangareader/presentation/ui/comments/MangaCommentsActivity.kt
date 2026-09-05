package com.fire.mangareader.presentation.ui.comments

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fire.mangareader.data.local.CommentEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MangaCommentsActivity : ComponentActivity() {
    private val viewModel by viewModels<CommentsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val mangaUrl = intent.getStringExtra("mangaUrl") ?: ""
        viewModel.setMangaUrl(mangaUrl)
        
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFFE21C42), // MangaSlayer red
                    background = Color(0xFF121212),
                    surface = Color(0xFF1E1E1E),
                    onPrimary = Color.White,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                CommentsScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(viewModel: CommentsViewModel) {
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    
    var showRules by remember { mutableStateOf(true) } // In a real app, read from DataStore/SharedPreferences

    if (showRules) {
        RulesDialog(onDismiss = { showRules = false })
    }

    var showSortDialog by remember { mutableStateOf(false) }
    
    if (showSortDialog) {
        SortCommentsDialog(
            onDismissRequest = { showSortDialog = false },
            onSortSelected = { sort, hide ->
                viewModel.changeSortOption(sort)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("التعليقات (Comments)") },
                actions = {
                    IconButton(onClick = { showSortDialog = true }) {
                        Icon(androidx.compose.material.icons.Icons.Default.MoreVert, contentDescription = "ترتيب")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            CommentInputBar(onSend = { content, isSpoiler -> 
                viewModel.addComment(content, isSpoiler) 
            })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (comments.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("لا توجد تعليقات. كن أول من يعلق!", color = Color.Gray)
                    }
                }
            } else {
                items(comments, key = { it.id }) { comment ->
                    CommentItem(
                        comment = comment,
                        onLike = { viewModel.toggleLike(comment) },
                        onDislike = { viewModel.toggleDislike(comment) },
                        onDelete = { viewModel.deleteComment(comment.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SortDropdown(currentSort: String, onSortChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Sort")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("الأحدث (Newest)") },
                onClick = { onSortChange("newest"); expanded = false }
            )
            DropdownMenuItem(
                text = { Text("الأقدم (Oldest)") },
                onClick = { onSortChange("oldest"); expanded = false }
            )
            DropdownMenuItem(
                text = { Text("أفضل التعليقات (Most Liked)") },
                onClick = { onSortChange("most_liked"); expanded = false }
            )
        }
    }
}

@Composable
fun CommentItem(
    comment: CommentEntity,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onDelete: () -> Unit
) {
    var spoilerRevealed by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    
    if (showReportDialog) {
        ReportCommentDialog(
            onDismissRequest = { showReportDialog = false },
            onReportSubmitted = { reason -> 
                // Handle report logic
                showReportDialog = false
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar Placeholder
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = comment.userName.first().toString().uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = comment.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = formatTime(comment.timestamp),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.Gray)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        if (comment.userId == "current_user_123") {
                            DropdownMenuItem(text = { Text("Delete") }, onClick = { onDelete(); showMenu = false })
                        } else {
                            DropdownMenuItem(text = { Text("Report") }, onClick = { showReportDialog = true; showMenu = false })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (comment.isSpoiler && !spoilerRevealed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x33E21C42))
                        .clickable { spoilerRevealed = true }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "التعليق يحتوي على حرق ، اضغط هنا للمشاهدة",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = comment.content,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Like Button
                IconButton(onClick = onLike, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (comment.isLikedByUser) Icons.Filled.KeyboardArrowUp else Icons.Outlined.KeyboardArrowUp,
                        contentDescription = "Like",
                        tint = if (comment.isLikedByUser) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(text = "${comment.likes}", fontSize = 14.sp, color = Color.Gray)
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Dislike Button
                IconButton(onClick = onDislike, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (comment.isDislikedByUser) Icons.Filled.KeyboardArrowDown else Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Dislike",
                        tint = if (comment.isDislikedByUser) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(text = "${comment.dislikes}", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun CommentInputBar(onSend: (String, Boolean) -> Unit) {
    var text by remember { mutableStateOf("") }
    var isSpoiler by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier
            .navigationBarsPadding()
            .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isSpoiler,
                    onCheckedChange = { isSpoiler = it },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Text(text = "حرق أحداث (Spoiler)", fontSize = 14.sp, color = Color.Gray)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("أضف تعليقاً...") },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.DarkGray
                    )
                )
                FloatingActionButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSend(text, isSpoiler)
                            text = ""
                            isSpoiler = false
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun RulesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* Must click accept */ },
        title = {
            Text(text = "قوانين التعليقات \uD83D\uDCDC", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("يرجى قراءة القوانين التالية لتجنب الحظر:")
                Text("• يمنع حرق الأحداث بدون استخدام زر الحرق.")
                Text("• يمنع السب أو الشتم أو التعليقات المسيئة.")
                Text("• يمنع وضع إعلانات أو روابط خارجية.")
                Text("مخالفة القوانين تعرض حسابك للحظر النهائي.")
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("موافق")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface
    )
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
