import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

admin_intent = """
            SettingsHeader(title = "الإدارة")
            SettingsItem(
                title = "لوحة تحكم المسؤول",
                onClick = {
                    context.startActivity(Intent(context, com.fire.mangareader.presentation.activity.AdminDashboardActivity::class.java))
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Other Settings
"""

content = content.replace('// Other Settings', admin_intent.strip())

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/settings/SettingsScreen.kt", "w") as f:
    f.write(content)

