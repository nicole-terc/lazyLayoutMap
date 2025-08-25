package dev.nstv.lazylayoutmap.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import dev.nstv.lazylayoutmap.ui.map.Tile

@Composable
fun LazyMapScreen(
    modifier: Modifier = Modifier,
) {
    LazyMap(
        modifier = modifier.fillMaxSize(),
        backgroundPath = "background.png",
        tiles = listOf(
            Tile(
                id = "castle",
                path = "tiles/castle.png",
                offset = Offset(0f, 0f),
                size = IntSize(300, 300),
                zoomLevelStart = 1f,
                zoomLevelEnd = 2.99f,
            ),
            Tile(
                id = "house1",
                path = "tiles/house.png",
                offset = Offset(0f, 0f),
                size = IntSize(200, 200),
                zoomLevelStart = 3f,
                zoomLevelEnd = 10f,
            ),
            Tile(
                id = "house2",
                path = "tiles/house.png",
                offset = Offset(100f, 0f),
                size = IntSize(200, 200),
                zoomLevelStart = 3f,
                zoomLevelEnd = 10f,
            ),
            Tile(
                id = "house3",
                path = "tiles/house.png",
                offset = Offset(50f, 50f),
                size = IntSize(200, 200),
                zoomLevelStart = 3f,
                zoomLevelEnd = 10f,
            ),
            Tile(
                id = "tree",
                path = "tiles/tree.png",
                offset = Offset(100f, 100f),
                size = IntSize(200, 200),
                zoomLevelStart = 2f,
                zoomLevelEnd = 10f,
                fullSizeZoomLevel = 1f,
            ),
        )
    )
}