with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "r") as f:
    content = f.read()

import re

# Patch CommentInputBar
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
                // Spoiler Button (Fire Icon)
                IconButton(onClick = { isSpoiler = !isSpoiler }) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = if (isSpoiler) android.R.drawable.ic_menu_close_clear_cancel else android.R.drawable.ic_menu_help), // Placeholder for fire/spoiler icon
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
                                    // Reset after mock delay
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

old_input_bar_pattern = r"@Composable\s*fun CommentInputBar\(onSend: \(String, Boolean\) -> Unit\)\s*\{.*?\}\s*\}\s*\}\s*\}"
# We'll use simple replace to avoid complex regex for compose functions. Let's find it.

# Actually, the file is available, we can just replace. 
