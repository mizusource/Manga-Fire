import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "r") as f:
    content = f.read()

# First replace CommentItem
new_comment_item = """@Composable
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
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(comment.author.first().toString(), color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(comment.author, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(SimpleDateFormat("dd/MM/yyyy HH:mm").format(comment.timestamp), fontSize = 12.sp, color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (comment.isSpoiler && !spoilerRevealed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Red.copy(alpha = 0.1f))
                        .clickable { spoilerRevealed = true }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚠️ هذا التعليق يحتوي على حرق للأحداث. انقر لإظهاره.", color = Color.Red, fontSize = 14.sp)
                }
            } else {
                Text(
                    text = comment.content,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action Buttons matching widget_comment_action.xml
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onLike, modifier = Modifier.size(32.dp)) {
                    Icon(androidx.compose.material.icons.Icons.Default.KeyboardArrowUp, contentDescription = "أعجبني")
                }
                Text("12", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                
                Spacer(modifier = Modifier.width(16.dp))
                
                IconButton(onClick = onDislike, modifier = Modifier.size(32.dp)) {
                    Icon(androidx.compose.material.icons.Icons.Default.KeyboardArrowDown, contentDescription = "لم يعجبني")
                }
                Text("2", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                
                Spacer(modifier = Modifier.width(16.dp))
                
                IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                    Icon(androidx.compose.material.icons.Icons.Default.ChatBubbleOutline, contentDescription = "رد")
                }
                Text("5", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                
                Spacer(modifier = Modifier.weight(1f))
                
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                        Icon(androidx.compose.material.icons.Icons.Default.MoreVert, contentDescription = "خيارات")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("إبلاغ عن التعليق") },
                            onClick = { 
                                showMenu = false
                                showReportDialog = true
                            },
                            leadingIcon = { Icon(androidx.compose.material.icons.Icons.Default.Report, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("حذف التعليق", color = MaterialTheme.colorScheme.error) },
                            onClick = { 
                                showMenu = false
                                onDelete() 
                            },
                            leadingIcon = { Icon(androidx.compose.material.icons.Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                }
            }
        }
    }
}"""

old_comment_item_pattern = r"@Composable\s*fun CommentItem\(.*?fun CommentInputBar"
comment_item_match = re.search(old_comment_item_pattern, content, flags=re.DOTALL)
if comment_item_match:
    content = content[:comment_item_match.start()] + new_comment_item + "\n\n@Composable\nfun CommentInputBar" + content[comment_item_match.end():]

new_input_bar = """@Composable
fun CommentInputBar(onSend: (String, Boolean) -> Unit) {
    var text by remember { mutableStateOf("") }
    var isSpoiler by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }
    
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier
            .navigationBarsPadding()
            .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Spoiler Button (Fire Icon Placeholder)
                IconButton(onClick = { isSpoiler = !isSpoiler }) {
                    Icon(
                        androidx.compose.material.icons.Icons.Default.Warning,
                        contentDescription = "سبويلر",
                        tint = if (isSpoiler) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Input Field
                androidx.compose.foundation.text.BasicTextField(
                    value = text,
                    onValueChange = { if (it.length <= 500) text = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    ),
                    decorationBox = { innerTextField ->
                        if (text.isEmpty()) {
                            Text("أضف تعليقاً...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        innerTextField()
                    }
                )
                
                // Send Button with Flipper
                Box(modifier = Modifier.padding(end = 8.dp), contentAlignment = Alignment.Center) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick = {
                                if (text.isNotBlank()) {
                                    isSending = true
                                    onSend(text, isSpoiler)
                                    text = ""
                                    isSpoiler = false
                                    isSending = false
                                }
                            },
                            enabled = text.isNotBlank()
                        ) {
                            Icon(
                                androidx.compose.material.icons.Icons.AutoMirrored.Filled.Send,
                                contentDescription = "إرسال",
                                tint = if (text.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                }
            }
            
            // Text Limit Counter
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "${text.length}/500",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (text.length >= 490) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, end = 12.dp)
                )
            }
        }
    }
}"""

old_input_bar_pattern = r"@Composable\s*fun CommentInputBar\(onSend: \(String, Boolean\) -> Unit\)\s*\{.*\Z"
content = re.sub(old_input_bar_pattern, new_input_bar, content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "w") as f:
    f.write(content)
