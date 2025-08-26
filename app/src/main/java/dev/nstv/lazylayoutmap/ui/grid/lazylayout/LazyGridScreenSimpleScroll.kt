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
import androidx.compose.ui.unit.Constraints
import dev.nstv.lazylayoutmap.ui.grid.griditem.CustomGridItem
import dev.nstv.lazylayoutmap.ui.grid.griditem.ITEMS_PER_ROW
import dev.nstv.lazylayoutmap.ui.grid.griditem.rememberGridItems

@Composable
fun LazyGridScreenSimpleScroll(
    modifier: Modifier = Modifier,
    constrainScroll: Boolean = false,
) {
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
                        itemsPerRow = itemsPerRow + itemsPerRow
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