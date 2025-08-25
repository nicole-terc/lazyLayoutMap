package dev.nstv.lazylayoutmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.nstv.lazylayoutmap.ui.MainContent
import dev.nstv.lazylayoutmap.ui.theme.LazyLayoutMapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LazyLayoutMapTheme {
                MainContent()
            }
        }
    }
}

