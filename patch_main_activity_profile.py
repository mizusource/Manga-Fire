import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

profile_route_bad = '''                        composable("profile") {
                            com.fire.mangareader.presentation.ui.screens.profile.ProfileScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }'''
profile_route_good = '''                        composable("profile") {
                            com.fire.mangareader.presentation.ui.screens.profile.ProfileScreen(
                                onDownloadsClick = { navController.navigate("downloads") },
                                onNotificationsClick = { navController.navigate("notifications") }
                            )
                        }'''

content = content.replace(profile_route_bad, profile_route_good)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)

