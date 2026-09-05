import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

content = re.sub(r'composable\("settings"\)\s*\{\s*SettingsScreen\([^)]*\)\s*\}', 'composable("settings") { SettingsScreen() }', content)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
