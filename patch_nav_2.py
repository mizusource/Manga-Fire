with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

nav_host_replacement = """
                        composable("settings") { 
                            ProfileScreen(
                                onDownloadsClick = { navController.navigate("downloads") },
                                onNotificationsClick = { navController.navigate("notifications") }
                            ) 
                        }
                        composable("notifications") {
                            NotificationsScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
"""

content = content.replace("""                        composable("settings") { 
                            ProfileScreen(
                                onDownloadsClick = { navController.navigate("downloads") }
                            ) 
                        }""", nav_host_replacement.strip())

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
