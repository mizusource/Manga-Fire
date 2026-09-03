package com.fire.mangareader.presentation.activity

import android.content.Intent
import com.fire.mangareader.presentation.activity.MangaDetailActivity
import com.fire.mangareader.presentation.activity.ChapterReaderActivity
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
import com.fire.mangareader.presentation.ui.screens.library.LibraryScreen
import com.fire.mangareader.presentation.ui.screens.profile.ProfileScreen
import com.fire.mangareader.presentation.ui.screens.notifications.NotificationsScreen


import com.fire.mangareader.presentation.ui.screens.downloads.DownloadsScreen

class MainComposeActivity : ComponentActivity() {

    private fun decodeUrl(id: String): String {
        if (id.startsWith("http://") || id.startsWith("https://")) return id
        return try {
            String(android.util.Base64.decode(id, android.util.Base64.URL_SAFE))
        } catch (e: Exception) {
            id
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Sync FCM token
        if (com.fire.mangareader.data.network.SupabaseManager.getInstance(this).isLoggedIn) {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    com.fire.mangareader.data.network.SupabaseManager.getInstance(this).updateFcmToken(token)
                }
            }
        }

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
                                onMangaClick = { mangaId: String, mangaTitle: String, mangaCover: String -> 
                                    val intent = Intent(this@MainComposeActivity, MangaDetailActivity::class.java).apply {
                                        putExtra("mangaUrl", decodeUrl(mangaId))
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                }
                            ) 
                        }
                        composable("search") { 
                            SearchScreen(
                                onMangaClick = { mangaId: String, mangaTitle: String, mangaCover: String -> 
                                    val intent = Intent(this@MainComposeActivity, MangaDetailActivity::class.java).apply {
                                        putExtra("mangaUrl", decodeUrl(mangaId))
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                }
                            ) 
                        }
                        composable("library") { 
                            LibraryScreen(
                                onMangaClick = { mangaId: String, mangaTitle: String, mangaCover: String -> 
                                    val intent = Intent(this@MainComposeActivity, MangaDetailActivity::class.java).apply {
                                        putExtra("mangaUrl", decodeUrl(mangaId))
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                },
                                onChapterClick = { chapterId: String, mangaId: String, chapterTitle: String, mangaTitle: String, mangaCover: String -> 
                                    val intent = Intent(this@MainComposeActivity, ChapterReaderActivity::class.java).apply {
                                        putExtra("chapterUrl", decodeUrl(chapterId))
                                        putExtra("mangaUrl", decodeUrl(mangaId))
                                        putExtra("chapterTitle", chapterTitle)
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                }
                            ) 
                        }
                        composable("downloads") {
                            DownloadsScreen(
                                onChapterClick = { chapterId: String, mangaId: String, chapterTitle: String, mangaTitle: String, mangaCover: String -> 
                                    val intent = Intent(this@MainComposeActivity, ChapterReaderActivity::class.java).apply {
                                        putExtra("chapterUrl", decodeUrl(chapterId))
                                        putExtra("mangaUrl", decodeUrl(mangaId))
                                        putExtra("chapterTitle", chapterTitle)
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                }
                            )
                        }
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
fun SettingsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("الإعدادات (Settings)", style = MaterialTheme.typography.titleLarge)
    }
}
