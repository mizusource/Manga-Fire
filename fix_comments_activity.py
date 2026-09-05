with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "r") as f:
    content = f.read()

# Fix unresolved references in MangaCommentsActivity
content = content.replace("comment.author.first()", "comment.userName.first()")
content = content.replace("comment.author", "comment.userName")
content = content.replace("androidx.compose.material.icons.Icons.Default.ChatBubbleOutline", "androidx.compose.material.icons.outlined.ChatBubbleOutline")
content = content.replace("androidx.compose.material.icons.Icons.Default.Report", "androidx.compose.material.icons.filled.Warning")
content = content.replace("androidx.compose.material.icons.Icons.Default.Delete", "androidx.compose.material.icons.filled.Delete")
content = content.replace("androidx.compose.material.icons.Icons.Default.Warning", "androidx.compose.material.icons.filled.Warning")
content = content.replace("androidx.compose.material.icons.Icons.AutoMirrored.Filled.Send", "androidx.compose.material.icons.automirrored.filled.Send")

# Add missing Dialogs
dialogs = """
@Composable
fun RulesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("قوانين التعليقات", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("1. يمنع الشتم والسب بأي شكل من الأشكال.")
                Text("2. يرجى استخدام زر حرق الأحداث عند كتابة أحداث مستقبلية.")
                Text("3. احترام آراء الآخرين.")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("موافق")
            }
        }
    )
}

@Composable
fun ReportCommentDialog(onDismissRequest: () -> Unit, onReportSubmitted: (String) -> Unit) {
    var selectedReason by remember { mutableStateOf("حرق أحداث غير مضلل") }
    val reasons = listOf("حرق أحداث غير مضلل", "شتائم وإهانة", "تعليق خارج الموضوع", "سبام / إعلان")
    
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("إبلاغ عن التعليق", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Text(reason, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onReportSubmitted(selectedReason) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("إرسال البلاغ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("إلغاء")
            }
        }
    )
}
"""

if "fun RulesDialog" not in content:
    content += "\n" + dialogs

with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "w") as f:
    f.write(content)
