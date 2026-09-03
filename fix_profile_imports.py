with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "r") as f:
    content = f.read()

imports = """
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Refresh
import android.widget.Toast
"""
if "kotlinx.coroutines.launch" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", imports + "import androidx.compose.runtime.Composable")
    with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "w") as f:
        f.write(content)
