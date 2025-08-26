package dev.nstv.lazylayoutmap.ui.grid.nonlazy

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberScrollable2DState
import androidx.compose.foundation.gestures.scrollable2D
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
import androidx.compose.ui.layout.Layout
import dev.nstv.lazylayoutmap.ui.grid.griditem.CustomGridItem
import dev.nstv.lazylayoutmap.ui.grid.griditem.GridItemView
import dev.nstv.lazylayoutmap.ui.grid.griditem.ITEMS_PER_ROW
import dev.nstv.lazylayoutmap.ui.grid.griditem.rememberGridItems
import kotlin.math.max
import kotlin.math.min

@Composable
fun NonLazyGridScreenWithScroll(
    modifier: Modifier = Modifier,
    constrainScroll: Boolean = true,
) {

    var itemsPerRow by remember { mutableIntStateOf(ITEMS_PER_ROW) }

    val items: List<CustomGridItem> = rememberGridItems(itemsPerRow)

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

                    // Calculate the scrollable bounds
                    // Max scroll is when the content's right/bottom edge aligns with the layout's right/bottom edge
                    // Min scroll is when the content's left/top edge aligns with the layout's left/top edge
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


    val content = @Composable {
        items.forEach { GridItemView(item = it) }
    }

    Layout(
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
        content = content,
    ) { measurables, constraints ->
        layoutWidth = constraints.maxWidth
        layoutHeight = constraints.maxHeight

        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        // Calculate actual content width and height
        var maxContentX = 0
        var maxContentY = 0
        if (placeables.isNotEmpty()) {
            items.forEachIndexed { index, item ->
                val placeable = placeables[index]
                maxContentX = max(maxContentX, item.x + placeable.width)
                maxContentY = max(maxContentY, item.y + placeable.height)
            }
        }
        contentWidth = maxContentX
        contentHeight = maxContentY


        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                val item = items[index]
                placeable.placeRelative(item.x, item.y)
            }
        }
    }
}
