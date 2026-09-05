with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

# Add imports
imports = """import com.fire.mangareader.presentation.ui.screens.auth.LoginScreen
import com.fire.mangareader.presentation.ui.screens.auth.RegisterScreen
import com.fire.mangareader.presentation.ui.screens.splash.SplashScreen
import com.fire.mangareader.presentation.ui.screens.admin.AdminDashboardScreen
import com.fire.mangareader.presentation.ui.screens.settings.StorageManagerScreen
"""
if "import com.fire.mangareader.presentation.ui.screens.auth.LoginScreen" not in content:
    content = content.replace("import com.fire.mangareader.presentation.ui.screens.home.HomeScreen", imports + "import com.fire.mangareader.presentation.ui.screens.home.HomeScreen")

# Change startDestination to splash
if 'startDestination = "home"' in content:
    content = content.replace('startDestination = "home"', 'startDestination = "splash"')

# Add composable routes before "home"
new_routes = """
                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = { navController.navigate("register") }
                            )
                        }
                        
                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = { navController.popBackStack() }
                            )
                        }
                        
                        composable("admin") {
                            AdminDashboardScreen(onBackClick = { navController.popBackStack() })
                        }
                        
                        composable("storage") {
                            StorageManagerScreen(onBackClick = { navController.popBackStack() })
                        }
"""
if 'composable("splash")' not in content:
    content = content.replace('composable("home") {', new_routes + '\n                        composable("home") {')

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
