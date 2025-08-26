package dev.nstv.lazylayoutmap.ui.grid.griditem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nstv.composablesheep.library.util.SheepColor

val DEFAULT_GRID_ITEM_SIZE = 100.dp

data class CustomGridItem(
    val id: String,
    val x: Int,
    val y: Int,
    val zoomLevel: Float = 1f,
    val color: Color = SheepColor.Green,
    val borderColor: Color = SheepColor.Black,
    val size: Dp = DEFAULT_GRID_ITEM_SIZE,
)
