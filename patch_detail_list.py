import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    text = f.read()

# Check if it already has state for the list sheet
if "var showListSheet by remember { mutableStateOf(false) }" not in text:
    state_addition = """
    val isFavorite by viewModel.isFavorite.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var showListSheet by remember { mutableStateOf(false) }
    var selectedList by remember { mutableStateOf("reading") }
    """
    text = re.sub(r'val isFavorite by viewModel\.isFavorite\.collectAsState\(\)\s*val error by viewModel\.error\.collectAsState\(\)', state_addition, text)

# Add Floating Action Button or Top bar action for "Add to list"
top_bar_pattern = r'IconButton\(onClick = onBackClick\) \{\s*Icon\(Icons\.AutoMirrored\.Filled\.ArrowBack, contentDescription = "رجوع", tint = Color\.White\)\s*\}'
new_top_bar = """
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showListSheet = true }) {
                        Icon(
                            imageVector = if (isFavorite) androidx.compose.material.icons.Icons.Default.Bookmark else androidx.compose.material.icons.Icons.Default.BookmarkBorder,
                            contentDescription = "أضف للقائمة",
                            tint = Color.White
                        )
                    }
"""

if "actions = {" not in text:
    text = re.sub(top_bar_pattern, new_top_bar.strip(), text)


# Add Bottom Sheet
bottom_sheet_code = """
        }
    }
    
    if (showListSheet) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showListSheet = false },
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text("إضافة إلى قائمتي", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(16.dp))
                
                val lists = listOf(
                    "reading" to "أقرأها حالياً",
                    "completed" to "مكتملة",
                    "plan_to_read" to "أرغب بقراءتها",
                    "dropped" to "متوقفة"
                )
                
                lists.forEach { (id, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                selectedList = id
                                viewModel.toggleFavorite(mangaId)
                                showListSheet = false
                                android.widget.Toast.makeText(context, "تم النقل إلى: $name", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = selectedList == id,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
"""

if "if (showListSheet) {" not in text:
    text = re.sub(r'\}\s*\}\s*\}\s*@Composable\s*fun ChapterItem', bottom_sheet_code.strip() + '\n\n@Composable\nfun ChapterItem', text)


text = text.replace("import androidx.compose.material.icons.filled.Warning", "import androidx.compose.material.icons.filled.Warning\nimport androidx.compose.material.icons.filled.Bookmark\nimport androidx.compose.material.icons.filled.BookmarkBorder")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(text)
