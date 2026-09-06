import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

# Pass NavController to SettingsScreen
content = content.replace('composable("settings") { SettingsScreen() }', 'composable("settings") { SettingsScreen(navController = navController) }')

# Add profile route
profile_route = '''
                        composable("profile") {
                            com.fire.mangareader.presentation.ui.screens.profile.ProfileScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("notifications")'''

content = content.replace('composable("notifications")', profile_route.strip())

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)

