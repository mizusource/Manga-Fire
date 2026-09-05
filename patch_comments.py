with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "r") as f:
    content = f.read()

# Add imports
if "import com.fire.mangareader.presentation.ui.comments.SortCommentsDialog" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", "import androidx.compose.runtime.Composable\nimport com.fire.mangareader.presentation.ui.comments.SortCommentsDialog\nimport com.fire.mangareader.presentation.ui.comments.ReportCommentDialog")

# Find CommentsScreen
old_topbar = """        topBar = {
            TopAppBar(
                title = { Text("التعليقات (Comments)") },
                actions = {
                    SortDropdown(currentSort = sortOption, onSortChange = { viewModel.changeSortOption(it) })
                },"""
new_topbar = """        var showSortDialog by remember { mutableStateOf(false) }
        
        if (showSortDialog) {
            SortCommentsDialog(
                onDismissRequest = { showSortDialog = false },
                onSortSelected = { sort, hide ->
                    viewModel.changeSortOption(sort)
                }
            )
        }

        topBar = {
            TopAppBar(
                title = { Text("التعليقات (Comments)") },
                actions = {
                    IconButton(onClick = { showSortDialog = true }) {
                        Icon(androidx.compose.material.icons.Icons.Default.MoreVert, contentDescription = "ترتيب")
                    }
                },"""
content = content.replace(old_topbar, new_topbar)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "w") as f:
    f.write(content)
