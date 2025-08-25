package dev.nstv.lazylayoutmap.ui.grid.nonlazy

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import dev.nstv.lazylayoutmap.ui.grid.griditem.CustomGridItem
import dev.nstv.lazylayoutmap.ui.grid.griditem.GridItemView
import dev.nstv.lazylayoutmap.ui.grid.griditem.rememberGridItems

@Composable
fun NonLazyGridScreen(
    modifier: Modifier = Modifier,
) {
    val items: List<CustomGridItem> = rememberGridItems()

    val content = @Composable {
        items.forEach { GridItemView(item = it) }
    }

    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                val item = items[index]
                placeable.placeRelative(item.x, item.y)
            }
        }
    }
}