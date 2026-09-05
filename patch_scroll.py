with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/search/SearchScreen.kt", "r") as f:
    content = f.read()

if "import androidx.compose.foundation.verticalScroll" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", "import androidx.compose.runtime.Composable\nimport androidx.compose.foundation.verticalScroll")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/search/SearchScreen.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

content = content.replace("import com.fire.mangareader.presentation.ui.screens.settings.SettingsScreen", "")
content = content.replace("import SettingsScreen", "")
if "import com.fire.mangareader.presentation.ui.screens.settings.SettingsScreen" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", "import androidx.compose.runtime.Composable\nimport com.fire.mangareader.presentation.ui.screens.settings.SettingsScreen")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
