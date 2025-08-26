package dev.nstv.lazylayoutmap.ui.grid.griditem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nstv.composablesheep.library.util.SheepColor

val DEFAULT_GRID_ITEM_SIZE = 100.dp
const val MIN_ZOOM_LEVEL = 1f
const val MAX_ZOOM_LEVEL = 10f

data class CustomGridItem(
    val id: String,
    val x: Int,
    val y: Int,
    val color: Color = SheepColor.Green,
    val borderColor: Color = SheepColor.Black,
    val size: Dp = DEFAULT_GRID_ITEM_SIZE,
    val minZoomLevel: Float = 1f,
    val maxZoomLevel: Float = MAX_ZOOM_LEVEL,
    val fullSizeZoomLevel: Float = minZoomLevel, // Zoom level at witch this item is at 100% size, useful for aligning different items at different zoom levels
)
