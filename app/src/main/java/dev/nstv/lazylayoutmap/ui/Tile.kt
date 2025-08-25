package dev.nstv.lazylayoutmap.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

data class Tile(
    val id: String,
    val path: String,
    val offset: Offset,
    val size: IntSize,
    val zoomLevelStart: Float, // Percentage 100% = no zoom
    val zoomLevelEnd: Float,
)