package dev.nstv.lazylayoutmap.ui.grid.lazylayout

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberScrollable2DState
import androidx.compose.foundation.gestures.scrollable2D
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.util.fastForEachIndexed
import dev.nstv.lazylayoutmap.ui.grid.griditem.CustomGridItem
import dev.nstv.lazylayoutmap.ui.grid.griditem.DEFAULT_GRID_ITEM_SIZE
import dev.nstv.lazylayoutmap.ui.grid.griditem.ITEMS_PER_ROW
import dev.nstv.lazylayoutmap.ui.grid.griditem.ITEM_INCREASE_FACTOR
import dev.nstv.lazylayoutmap.ui.grid.griditem.rememberGridItems

@Composable
fun LazyGridScreenScrollZoom(
    modifier: Modifier = Modifier,
    constrainScroll: Boolean = false,
) {
    val density = LocalDensity.current
    val defaultItemSize = with(density) { DEFAULT_GRID_ITEM_SIZE.toPx() }

    var itemsPerRow by remember { mutableIntStateOf(ITEMS_PER_ROW) }

    val items: List<CustomGridItem> = rememberGridItems(itemsPerRow)
    val itemProvider = remember(items) { LazyGridItemProvider(items) }

    var offset by remember { mutableStateOf(Offset.Zero) }
    var contentWidth by remember { mutableIntStateOf(0) }
    var contentHeight by remember { mutableIntStateOf(0) }
    var layoutWidth by remember { mutableIntStateOf(0) }
    var layoutHeight by remember { mutableIntStateOf(0) }

    val scrollableModifier =
        Modifier.scrollable2D(
            state = rememberScrollable2DState { delta ->
                if (constrainScroll) {
                    val newOffset = offset + delta

                    val minScrollX = -(contentWidth - layoutWidth).coerceAtLeast(0).toFloat()
                    val maxScrollX = 0f
                    val minScrollY = -(contentHeight - layoutHeight).coerceAtLeast(0).toFloat()
                    val maxScrollY = 0f

                    // Apply constraints
                    val constrainedX = newOffset.x.coerceIn(minScrollX, maxScrollX)
                    val constrainedY = newOffset.y.coerceIn(minScrollY, maxScrollY)

                    val consumed = Offset(constrainedX - offset.x, constrainedY - offset.y)
                    offset = Offset(constrainedX, constrainedY)
                    consumed // Return the consumed delta
                } else {
                    offset = offset + delta
                    delta // Return the original delta if not constraining
                }
            }
        )

    LazyLayout(
        modifier = modifier
            .then(scrollableModifier)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        itemsPerRow *= ITEM_INCREASE_FACTOR
                    }
                )
            },
        itemProvider = { itemProvider },
    ) { constraints ->
        val viewportWidth = constraints.maxWidth
        val viewportHeight = constraints.maxHeight

        val placeablesIndexed: List<Pair<Int, Placeable>> = buildList {
            items.fastForEachIndexed { itemIndex, item ->
                if (
                    intersectsViewport(
                        left = item.x + offset.x,
                        top = item.y + offset.y,
                        width = defaultItemSize,
                        height = defaultItemSize,
                        viewportWidth = viewportWidth,
                        viewportHeight = viewportHeight,
                    )
                ) {
                    val placeable: Placeable = compose(itemIndex).first().measure(Constraints())
                    add(itemIndex to placeable)
                }
            }
        }

        layout(viewportWidth, viewportHeight) {
            placeablesIndexed.forEach {
                val item = items[it.first]
                it.second.placeRelative(item.x, item.y)
            }
        }
    }
}