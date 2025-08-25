package dev.nstv.lazylayoutmap.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

data class Tile(
    val id: String,
    val path: String,
    val offset: Offset,
    val size: IntSize,
    val zoomLevelStart: Float,
    val zoomLevelEnd: Float,
    val fullSizeZoomLevel: Float = zoomLevelStart, // Zoom level at witch this item is at 100% size, useful for aligning different items at different zoom levels
)