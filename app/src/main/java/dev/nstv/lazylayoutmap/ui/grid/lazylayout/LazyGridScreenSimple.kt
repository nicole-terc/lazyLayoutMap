package dev.nstv.lazylayoutmap.ui.grid.lazylayout

import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Constraints
import dev.nstv.lazylayoutmap.ui.grid.griditem.CustomGridItem
import dev.nstv.lazylayoutmap.ui.grid.griditem.rememberGridItems

@Composable
fun LazyGridScreenSimple(
    modifier: Modifier = Modifier,
) {
    val items: List<CustomGridItem> = rememberGridItems()
    val itemProvider = remember(items) { LazyGridItemProvider(items) }

    LazyLayout(
        modifier = modifier,
        itemProvider = { itemProvider },
    ) { constraints ->
        val viewportWidth = constraints.maxWidth
        val viewportHeight = constraints.maxHeight

        val placeables = items.mapIndexed { index, item ->
            compose(index).first().measure(Constraints())
        }

        layout(viewportWidth, viewportHeight) {
            placeables.forEachIndexed { index, placeable ->
                val item = items[index]
                placeable.placeRelative(item.x, item.y)
            }
        }
    }
}