package dev.nstv.lazylayoutmap.ui.grid.griditem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.fastRoundToInt

const val ITEMS_PER_ROW = 10
const val ITEM_INCREASE_FACTOR = 2

val MAX_ITEMS_PER_ROW = 400

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
                        id = "${y * adjustedItemsPerRow + x}",
                        x = x * gridItemWidth,
                        y = y * gridItemWidth,
                    )
                )
            }
        }
    }
}
