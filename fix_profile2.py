import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "r") as f:
    content = f.read()

# Fix INSTANCE
content = content.replace("ParserConfigManager.INSTANCE", "ParserConfigManager")

# Fix imports
if "import android.widget.Toast" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", "import android.widget.Toast\nimport kotlinx.coroutines.launch\nimport androidx.compose.material.icons.filled.Refresh\nimport androidx.compose.runtime.Composable")

# Check if context is available in coroutineScope
# And wait, the Dialog components need to be inside @Composable context, which they are. But the error says:
# `@Composable invocations can only happen from the context of a @Composable function`
# Let's look at where they are placed. They were placed inside `Column {` in the lazy column maybe? No, I put them outside `Scaffold` probably.
# Let's check where they were inserted.

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "w") as f:
    f.write(content)
