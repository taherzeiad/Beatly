package com.taher.beatly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.taher.beatly.navigation.BeatlyNavGraph
import com.taher.beatly.ui.theme.BeatlyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Allow content to draw behind system bars (status + nav bar)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            BeatlyTheme {
                BeatlyNavGraph()
            }
        }
    }
}