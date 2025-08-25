package dev.nstv.lazylayoutmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import dev.nstv.lazylayoutmap.ui.LazyMap
import dev.nstv.lazylayoutmap.ui.Tile
import dev.nstv.lazylayoutmap.ui.theme.LazyLayoutMapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LazyLayoutMapTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LazyMap(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        backgroundPath = "background.png",
                        tiles = listOf(
                            Tile(
                                id = "castle",
                                path = "tiles/castle.png",
                                offset = Offset(0f, 0f),
                                size = IntSize(300, 300),
                                zoomLevelStart = 0f,
                                zoomLevelEnd = 299f,
                            ),
                            Tile(
                                id = "house1",
                                path = "tiles/house.png",
                                offset = Offset(0f, 0f),
                                size = IntSize(200, 200),
                                zoomLevelStart = 300f,
                                zoomLevelEnd = 1000f,
                            ),
                            Tile(
                                id = "house2",
                                path = "tiles/house.png",
                                offset = Offset(100f, 0f),
                                size = IntSize(200, 200),
                                zoomLevelStart = 300f,
                                zoomLevelEnd = 1000f,
                            ),
                            Tile(
                                id = "house3",
                                path = "tiles/house.png",
                                offset = Offset(50f, 50f),
                                size = IntSize(200, 200),
                                zoomLevelStart = 300f,
                                zoomLevelEnd = 1000f,
                            ),
                            Tile(
                                id = "tree",
                                path = "tiles/tree.png",
                                offset = Offset(100f, 100f),
                                size = IntSize(200, 200),
                                zoomLevelStart = 200f,
                                zoomLevelEnd = 1000f,
                            ),
                        )
                    )
                }
            }
        }
    }
}

