with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

content = content.replace("com.fire.mangareader.presentation.ui.screens.settings.SettingsScreen(", "com.fire.mangareader.presentation.ui.screens.settings.SettingsScreen(")
if "import com.fire.mangareader.presentation.ui.screens.settings.SettingsScreen" not in content:
    content = content.replace("import com.fire.mangareader.presentation.ui.screens.profile.ProfileScreen", "import com.fire.mangareader.presentation.ui.screens.profile.ProfileScreen\nimport com.fire.mangareader.presentation.ui.screens.settings.SettingsScreen")

content = content.replace("com.fire.mangareader.presentation.ui.screens.settings.SettingsScreen", "SettingsScreen")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
