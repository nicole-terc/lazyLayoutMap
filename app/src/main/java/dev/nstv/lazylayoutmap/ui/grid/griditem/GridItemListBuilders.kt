package dev.nstv.lazylayoutmap.ui.grid.griditem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.fastRoundToInt

const val ITEMS_PER_ROW = 10

@Composable
fun rememberGridItems(
    itemsPerRow: Int = ITEMS_PER_ROW,
    itemSize: Dp = DEFAULT_GRID_SIZE,
    density: Density = LocalDensity.current,
) = remember {
    val gridItemWidth = with(density) { itemSize.toPx().fastRoundToInt() }

    buildList {
        for (y in 0 until itemsPerRow) {
            for (x in 0 until itemsPerRow) {
                add(
                    CustomGridItem(
                        id = "${y * itemsPerRow + x}",
                        x = x * gridItemWidth,
                        y = y * gridItemWidth,
                    )
                )
            }
        }
    }
}
