package dev.nstv.lazylayoutmap.ui.grid.lazylayout

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastRoundToInt
import dev.nstv.lazylayoutmap.ui.grid.griditem.CustomGridItem
import dev.nstv.lazylayoutmap.ui.grid.griditem.DEFAULT_GRID_ITEM_SIZE
import dev.nstv.lazylayoutmap.ui.grid.griditem.ITEMS_PER_ROW
import dev.nstv.lazylayoutmap.ui.grid.griditem.ITEM_INCREASE_FACTOR
import dev.nstv.lazylayoutmap.ui.grid.griditem.MAX_ZOOM_LEVEL
import dev.nstv.lazylayoutmap.ui.grid.griditem.MIN_ZOOM_LEVEL
import dev.nstv.lazylayoutmap.ui.grid.griditem.rememberGridItems


@Composable
fun LazyGridScreenScrollZoom(
    modifier: Modifier = Modifier,
    constrainScroll: Boolean = false,
    minZoomLevel: Float = MIN_ZOOM_LEVEL,
    maxZoomLevel: Float = MAX_ZOOM_LEVEL,
) {
    val density = LocalDensity.current

    // Camera
    var scale by remember { mutableFloatStateOf(1f) }  // 1f == 100%
    var pan by remember { mutableStateOf(Offset.Zero) }
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }

    // Items
    val defaultItemSize = with(density) { DEFAULT_GRID_ITEM_SIZE.toPx() }
    var itemsPerRow by remember { mutableIntStateOf(ITEMS_PER_ROW) }
    val items: List<CustomGridItem> = rememberGridItems(itemsPerRow)
    val itemProvider = remember(items) {
        LazyGridItemProvider(
            items = items,
            zoomLevel = { scale }
        )
    }


    // Helper functions
    fun restartCamera() {
        scale = 1f
        pan = Offset.Zero
    }

    fun visibleByZoom(item: CustomGridItem, zoomLevel: Float): Boolean =
        zoomLevel in item.zoomLevelStart..item.zoomLevelEnd


    LazyLayout(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        itemsPerRow *= ITEM_INCREASE_FACTOR
                    },
                    onLongPress = {
                        restartCamera()
                    }
                )
            }
            .pointerInput(maxZoomLevel) {
                detectTransformGestures(panZoomLock = false) { centroid, panChange, zoomChange, _ ->
                    if (viewportSize.width == 0 || viewportSize.height == 0) return@detectTransformGestures

                    val old = scale
                    val newZoomLevel = (old * zoomChange).coerceIn(minZoomLevel, maxZoomLevel)

                    // focal lock: keep world point under centroid stationary
                    val worldFocus = (centroid - pan) / old
                    val panAfterZoom = centroid - worldFocus * newZoomLevel

                    scale = newZoomLevel
                    pan = panAfterZoom + panChange
                }
            },
        itemProvider = { itemProvider },
    ) { constraints ->
        val viewportWidth = constraints.maxWidth
        val viewportHeight = constraints.maxHeight
        viewportSize = IntSize(viewportWidth, viewportHeight)

        val panX = pan.x
        val panY = pan.y

        val placeablesAndCoordinates: List<Triple<Placeable, Int, Int>> = buildList {
            items.fastForEachIndexed { itemIndex, item ->

                if (!visibleByZoom(item, scale)) return@fastForEachIndexed

                // Size at zoomLevelStart is 100% of the size
                val adjustedScale = scale / item.fullSizeZoomLevel
                val itemSize = defaultItemSize * adjustedScale

                val itemLeft = item.x * scale + panX
                val itemTop = item.y * scale + panY

                if (
                    intersectsViewport(
                        left = itemLeft,
                        top = itemTop,
                        width = itemSize,
                        height = itemSize,
                        viewportWidth = viewportWidth,
                        viewportHeight = viewportHeight,
                    )
                ) {
                    val itemSizeInt = itemSize.fastRoundToInt()
                    val placeable: Placeable =
                        compose(itemIndex).first()
                            .measure(Constraints.fixed(itemSizeInt, itemSizeInt))
                    add(Triple(placeable, itemLeft.fastRoundToInt(), itemTop.fastRoundToInt()))
                }
            }
        }

        layout(viewportWidth, viewportHeight) {
            placeablesAndCoordinates.forEach { (placeable, x, y) ->
                placeable.place(x, y)
            }
        }
    }
}