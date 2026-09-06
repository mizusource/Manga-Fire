import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

content = content.replace("com.fire.mangareader.presentation.activity.AdminDashboardActivity::class.java", "com.fire.mangareader.presentation.activity.AdminLoginActivity::class.java")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/settings/SettingsScreen.kt", "w") as f:
    f.write(content)

