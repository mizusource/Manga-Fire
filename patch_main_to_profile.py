filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('import com.fire.mangareader.presentation.ui.screens.library.LibraryScreen',
                          'import com.fire.mangareader.presentation.ui.screens.library.LibraryScreen\nimport com.fire.mangareader.presentation.ui.screens.profile.ProfileScreen')

content = content.replace('composable("settings") { SettingsScreen() }', 'composable("settings") { ProfileScreen() }')

with open(filepath, 'w') as f:
    f.write(content)
