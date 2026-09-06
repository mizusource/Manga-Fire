import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

# Make SettingsScreen take NavController
content = content.replace("fun SettingsScreen(\n    onBackClick: () -> Unit = {}\n) {", "fun SettingsScreen(\n    navController: androidx.navigation.NavController,\n    onBackClick: () -> Unit = {}\n) {")

# Update SettingsItem onClick handlers
content = content.replace('''                onClick = {
                    context.startActivity(Intent(context, com.fire.mangareader.presentation.activity.ProfileActivity::class.java))
                }''', '''                onClick = { navController.navigate("profile") }''')

content = content.replace('''                onClick = {
                    context.startActivity(Intent(context, com.fire.mangareader.presentation.activity.NotificationsActivity::class.java))
                }''', '''                onClick = { navController.navigate("notifications") }''')

content = content.replace('''                onClick = {
                    context.startActivity(Intent(context, DownloadsActivity::class.java))
                }''', '''                onClick = { navController.navigate("downloads") }''')

content = content.replace('''                onClick = {
                    context.startActivity(Intent(context, StorageManagerActivity::class.java))
                }''', '''                onClick = { navController.navigate("storage") }''')

content = content.replace('''                onClick = {
                    context.startActivity(Intent(context, com.fire.mangareader.presentation.activity.AdminLoginActivity::class.java))
                }''', '''                onClick = { navController.navigate("admin") }''')

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/settings/SettingsScreen.kt", "w") as f:
    f.write(content)

