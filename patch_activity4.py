with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

content = content.replace('                        composable("settings") { SettingsScreen() }\n                            )\n                        }', '''                        composable("settings") { SettingsScreen() }
                        
                        composable("notifications") {
                            NotificationsScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }''')

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
