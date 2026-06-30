package com.taher.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.beatly.ui.theme.BeatlyTheme
import com.taher.myapplication.navigation.BeatlyNavGraph

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