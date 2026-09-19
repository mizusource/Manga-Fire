package com.fire.mangareader.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fire.mangareader.model.Chapter
import com.fire.mangareader.model.MangaDetails
import com.fire.mangareader.model.MangaItem
import com.fire.mangareader.ui.components.CloudflareResolverDialog
import com.fire.mangareader.ui.screens.DetailsScreen
import com.fire.mangareader.ui.screens.FavoritesScreen
import com.fire.mangareader.ui.screens.HomeScreen
import com.fire.mangareader.ui.screens.ReaderScreen

object Destinations {
    const val HOME = "home"
    const val DETAILS = "details"
    const val READER = "reader"
    const val FAVORITES = "favorites"
}

@Composable
fun MangaApp(viewModel: MangaViewModel) {
    val navController = rememberNavController()
    var selectedManga by remember { mutableStateOf<MangaItem?>(null) }
    var currentMangaDetails by remember { mutableStateOf<MangaDetails?>(null) }
    var selectedChapter by remember { mutableStateOf<Chapter?>(null) }

    val cloudflareUrl by viewModel.cloudflareTargetUrl.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Destinations.HOME,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            composable(Destinations.HOME) {
                HomeScreen(
                    viewModel = viewModel,
                    onMangaClick = { manga ->
                        selectedManga = manga
                        viewModel.loadMangaDetails(manga)
                        navController.navigate(Destinations.DETAILS)
                    },
                    onNavigateToFavorites = {
                        navController.navigate(Destinations.FAVORITES)
                    }
                )
            }

            composable(Destinations.DETAILS) {
                selectedManga?.let { manga ->
                    DetailsScreen(
                        manga = manga,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onChapterClick = { chapter, details ->
                            selectedChapter = chapter
                            currentMangaDetails = details
                            viewModel.loadChapterPages(chapter, details)
                            navController.navigate(Destinations.READER)
                        }
                    )
                }
            }

            composable(Destinations.READER) {
                val manga = currentMangaDetails
                val chapter = selectedChapter
                if (manga != null && chapter != null) {
                    ReaderScreen(
                        manga = manga,
                        initialChapter = chapter,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Destinations.FAVORITES) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onMangaClick = { manga ->
                        selectedManga = manga
                        viewModel.loadMangaDetails(manga)
                        navController.navigate(Destinations.DETAILS)
                    }
                )
            }
        }

        // Global Cloudflare solver dialog overlay
        cloudflareUrl?.let { url ->
            CloudflareResolverDialog(
                targetUrl = url,
                onSuccess = { viewModel.onCloudflareResolved() },
                onDismiss = { viewModel.dismissCloudflare() }
            )
        }
    }
}
