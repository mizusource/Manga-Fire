package com.fire.mangareader.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fire.mangareader.presentation.theme.MangaFireTheme
import com.fire.mangareader.presentation.ui.screens.detail.MangaDetailScreen
import com.fire.mangareader.presentation.ui.screens.home.HomeScreen
import com.fire.mangareader.presentation.ui.screens.search.SearchScreen
import com.fire.mangareader.presentation.ui.screens.reader.ChapterReaderScreen

class MainComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MangaFireTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Hide bottom bar on detail and reader screens
                val showBottomBar = currentRoute in listOf("home", "search", "library", "settings")

                Scaffold(
                    bottomBar = { if (showBottomBar) BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { 
                            HomeScreen(
                                onMangaClick = { mangaId -> navController.navigate("detail/$mangaId") }
                            ) 
                        }
                        composable("search") { 
                            SearchScreen(
                                onMangaClick = { mangaId -> navController.navigate("detail/$mangaId") }
                            ) 
                        }
                        composable("library") { LibraryScreen() }
                        composable("settings") { SettingsScreen() }
                        
                        composable(
                            "detail/{mangaId}",
                            arguments = listOf(navArgument("mangaId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val mangaId = backStackEntry.arguments?.getString("mangaId") ?: ""
                            MangaDetailScreen(
                                mangaId = mangaId,
                                onBackClick = { navController.popBackStack() },
                                onChapterClick = { chapterId -> navController.navigate("reader/$chapterId") }
                            )
                        }
                        
                        composable(
                            "reader/{chapterId}",
                            arguments = listOf(navArgument("chapterId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
                            ChapterReaderScreen(
                                chapterId = chapterId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "الرئيسية")
    object Search : BottomNavItem("search", Icons.Default.Search, "بحث")
    object Library : BottomNavItem("library", Icons.Default.Favorite, "مكتبتي")
    object Settings : BottomNavItem("settings", Icons.Default.Settings, "الإعدادات")
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Library,
        BottomNavItem.Settings
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun LibraryScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("المكتبة (Library)", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun SettingsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("الإعدادات (Settings)", style = MaterialTheme.typography.titleLarge)
    }
}
