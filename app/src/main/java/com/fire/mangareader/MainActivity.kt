package com.fire.mangareader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.fire.mangareader.ui.MangaApp
import com.fire.mangareader.ui.MangaViewModel
import com.fire.mangareader.ui.theme.MangaReaderTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MangaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MangaReaderTheme {
                MangaApp(viewModel = viewModel)
            }
        }
    }
}
