with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    content = f.read()

# Add imports
if "import com.fire.mangareader.presentation.ui.screens.detail.RatingDialog" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", "import androidx.compose.runtime.Composable\nimport com.fire.mangareader.presentation.ui.screens.detail.RatingDialog\nimport androidx.compose.runtime.mutableStateOf")

# Add showRatingDialog state
if "var showRatingDialog by remember { mutableStateOf(false) }" not in content:
    content = content.replace("var showListSheet by remember { mutableStateOf(false) }", "var showListSheet by remember { mutableStateOf(false) }\n    var showRatingDialog by remember { mutableStateOf(false) }")

# Add TopAppBar Rating Icon
old_top_bar = """navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors("""
new_top_bar = """navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showRatingDialog = true }) {
                        Icon(Icons.Default.Star, contentDescription = "تقييم", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors("""
content = content.replace(old_top_bar, new_top_bar)

# Add RatingDialog UI
dialog_code = """
        if (showRatingDialog) {
            RatingDialog(
                onDismissRequest = { showRatingDialog = false },
                onRatingSubmit = { story, characters, art ->
                    // Handle rating submission
                    showRatingDialog = false
                }
            )
        }
"""
if "if (showRatingDialog)" not in content:
    content = content.replace("if (showListSheet) {", dialog_code + "\n        if (showListSheet) {")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(content)
