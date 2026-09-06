import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

splash_old = '''                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    val destination = if (prefs.isLoggedIn()) "home" else "login"'''

splash_new = '''                        composable("splash") {
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val prefs = com.fire.mangareader.util.PreferenceManager(context)
                            SplashScreen(
                                onSplashFinished = {
                                    val destination = if (prefs.isLoggedIn()) "home" else "login"'''

content = content.replace(splash_old, splash_new)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)

