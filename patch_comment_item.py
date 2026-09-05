with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "r") as f:
    content = f.read()

old_comment_item_start = """fun CommentItem(
    comment: CommentEntity,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onDelete: () -> Unit
) {
    var spoilerRevealed by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }"""

new_comment_item_start = """fun CommentItem(
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
    }"""

content = content.replace(old_comment_item_start, new_comment_item_start)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "w") as f:
    f.write(content)
