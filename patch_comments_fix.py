with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "r") as f:
    content = f.read()

bad_scaffold = """    Scaffold(
        var showSortDialog by remember { mutableStateOf(false) }
        
        if (showSortDialog) {
            SortCommentsDialog(
                onDismissRequest = { showSortDialog = false },
                onSortSelected = { sort, hide ->
                    viewModel.changeSortOption(sort)
                }
            )
        }

        topBar = {"""

good_scaffold = """    var showSortDialog by remember { mutableStateOf(false) }
    
    if (showSortDialog) {
        SortCommentsDialog(
            onDismissRequest = { showSortDialog = false },
            onSortSelected = { sort, hide ->
                viewModel.changeSortOption(sort)
            }
        )
    }

    Scaffold(
        topBar = {"""

content = content.replace(bad_scaffold, good_scaffold)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "w") as f:
    f.write(content)
