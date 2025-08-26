package dev.nstv.lazylayoutmap.ui.grid.griditem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.fastRoundToInt
import dev.nstv.composablesheep.library.util.SheepColor
import kotlin.math.ceil

const val ITEMS_PER_ROW = 10
const val ITEM_INCREASE_FACTOR = 2
const val MAX_ITEMS_PER_ROW = 400
const val ZOOM_THRESHOLD = 0.5f

@Composable
fun rememberGridItems(
    itemsPerRow: Int = ITEMS_PER_ROW,
    itemSize: Dp = DEFAULT_GRID_ITEM_SIZE,
    density: Density = LocalDensity.current,
) = remember(itemSize, itemsPerRow, density) {

    val adjustedItemsPerRow = itemsPerRow.coerceAtMost(MAX_ITEMS_PER_ROW)

    val gridItemWidth = with(density) { itemSize.toPx().fastRoundToInt() }

    buildList {
        for (y in 0 until adjustedItemsPerRow) {
            for (x in 0 until adjustedItemsPerRow) {
                add(
                    CustomGridItem(
                        id = "#${y * adjustedItemsPerRow + x}",
                        x = x * gridItemWidth,
                        y = y * gridItemWidth,
                    )
                )
            }
        }
    }
}

val colorOptions = listOf(
    SheepColor.Green,
    SheepColor.Orange,
    SheepColor.Blue,
    Color.Yellow,
    Color.Cyan,
    SheepColor.Magenta,
    SheepColor.Purple,
)

@Composable
fun rememberGridItemsWithZoom(
    itemsPerRow: Int = 5,
    itemSize: Dp = DEFAULT_GRID_ITEM_SIZE,
    density: Density = LocalDensity.current,
    zoomLevels: List<Float> = listOf(1f, 2f, 4f, 8f),
) = remember(itemSize, itemsPerRow, density) {

    val adjustedItemsPerRow = itemsPerRow.coerceAtMost(MAX_ITEMS_PER_ROW)
    val gridItemWidth = with(density) { itemSize.toPx().fastRoundToInt() }

    val zoomLevelColors = buildList {
        for (i in 0 until zoomLevels.size) {
            add(colorOptions[i % colorOptions.size])
        }
    }

    buildList {
        zoomLevels.forEachIndexed { zoomLevelIndex, zoomLevel ->
            val zoomLevelItemsPerRow =
                adjustedItemsPerRow * (zoomLevelIndex + 1).coerceAtMost(MAX_ITEMS_PER_ROW)
            val minZoom =
                (if (zoomLevelIndex == 0) MIN_ZOOM_LEVEL else zoomLevels[zoomLevelIndex])
            val maxZoom =
                if (zoomLevelIndex == zoomLevels.lastIndex) MAX_ZOOM_LEVEL else zoomLevels[zoomLevelIndex + 1]

            for (y in 0 until zoomLevelItemsPerRow) {
                for (x in 0 until zoomLevelItemsPerRow) {
                    add(
                        CustomGridItem(
                            id = "${zoomLevel.fastRoundToInt()}:${y * zoomLevelItemsPerRow + x}",
                            x = x * gridItemWidth / minZoom.fastRoundToInt(),
                            y = y * gridItemWidth / minZoom.fastRoundToInt(),
                            minZoomLevel = minZoom,
                            maxZoomLevel = maxZoom,
                            color = zoomLevelColors[zoomLevelIndex],
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun rememberGridItemsWithZoomAdjustedColors(
    itemsPerRow: Int = 5,
    itemSize: Dp = DEFAULT_GRID_ITEM_SIZE,
    density: Density = LocalDensity.current,
    zoomLevels: List<Float> = listOf(1f, 2f, 4f, 8f),
) = remember(itemSize, itemsPerRow, density) {

    val adjustedItemsPerRow = itemsPerRow.coerceAtMost(MAX_ITEMS_PER_ROW)
    val gridItemWidth = with(density) { itemSize.toPx().fastRoundToInt() }


    buildList {
        zoomLevels.forEachIndexed { zoomLevelIndex, zoomLevel ->
            val expansionFactor = (ceil(zoomLevel / zoomLevels[0])).fastRoundToInt()
            val colorAlpha = 0.7f //+ zoomLevelIndex * 0.3f / zoomLevels.size

            val zoomLevelItemsPerRow = adjustedItemsPerRow * expansionFactor
            val previousZoom =
                (if (zoomLevelIndex == 0) MIN_ZOOM_LEVEL else zoomLevels[zoomLevelIndex])
            val nextZoom =
                if (zoomLevelIndex == zoomLevels.lastIndex) MAX_ZOOM_LEVEL else zoomLevels[zoomLevelIndex + 1]

            for (y in 0 until zoomLevelItemsPerRow) {
                for (x in 0 until zoomLevelItemsPerRow) {

                    val currentItemNumber = y * zoomLevelItemsPerRow + x

                    // Calculate the coordinates of this item in the original grid
                    val originalX = x / expansionFactor
                    val originalY = y / expansionFactor

                    // Calculate the item number in the original grid
                    val originalItemNumber = originalY * adjustedItemsPerRow + originalX

                    val color =
                        colorOptions[originalItemNumber % colorOptions.size].copy(alpha = colorAlpha)

                    add(
                        CustomGridItem(
                            id = "${zoomLevel.fastRoundToInt()}:${currentItemNumber}",
                            x = x * gridItemWidth / previousZoom.fastRoundToInt(),
                            y = y * gridItemWidth / previousZoom.fastRoundToInt(),
                            minZoomLevel = previousZoom - ZOOM_THRESHOLD,
                            maxZoomLevel = nextZoom + ZOOM_THRESHOLD,
                            fullSizeZoomLevel = zoomLevel,
                            color = color,
                        )
                    )
                }
            }
        }
    }.reversed()
}