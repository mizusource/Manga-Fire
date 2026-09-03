with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

if "import com.fire.mangareader.presentation.ui.screens.notifications.NotificationsScreen" not in content:
    content = content.replace("import com.fire.mangareader.presentation.ui.screens.profile.ProfileScreen", "import com.fire.mangareader.presentation.ui.screens.profile.ProfileScreen\nimport com.fire.mangareader.presentation.ui.screens.notifications.NotificationsScreen")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
