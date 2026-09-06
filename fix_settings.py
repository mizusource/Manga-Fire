import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

profile_intent = """
            SettingsHeader(title = "حسابي")
            SettingsItem(
                title = "الملف الشخصي",
                onClick = {
                    context.startActivity(Intent(context, com.fire.mangareader.presentation.activity.ProfileActivity::class.java))
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            SettingsItem(
                title = "الإشعارات",
                onClick = {
                    context.startActivity(Intent(context, com.fire.mangareader.presentation.activity.NotificationsActivity::class.java))
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            SettingsHeader(title = "مكتبتي")
"""

content = content.replace('SettingsHeader(title = "مكتبتي")', profile_intent.strip())

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/settings/SettingsScreen.kt", "w") as f:
    f.write(content)

