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
fun LazyGridScreenRealScroll(
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val defaultItemSize = with(density) { DEFAULT_GRID_ITEM_SIZE.toPx() }

    var itemsPerRow by remember { mutableIntStateOf(ITEMS_PER_ROW) }

    val items: List<CustomGridItem> = rememberGridItems(itemsPerRow, useColors = true)
    val itemProvider = remember(items) { LazyGridItemProvider(items, showText = false) }

    var offset by remember { mutableStateOf(Offset.Zero) }

    val scrollableModifier =
        Modifier.scrollable2D(
            state = rememberScrollable2DState { delta ->
                offset = offset + delta
                delta
            }
        )

    LazyLayout(
        modifier = modifier
            .then(scrollableModifier)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        itemsPerRow *= ITEM_INCREASE_FACTOR
                    },
                    onLongPress = {
                        offset = Offset.Zero
                    }
                )
            }
            .graphicsLayer {
                translationX = offset.x
                translationY = offset.y
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
            placeablesIndexed.forEach { (index, placeable) ->
                val item = items[index]
                placeable.placeRelative(item.x, item.y)
            }
        }
    }
}