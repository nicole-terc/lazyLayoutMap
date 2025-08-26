package dev.nstv.lazylayoutmap.ui.grid.lazylayout

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastRoundToInt
import dev.nstv.lazylayoutmap.ui.SHEEP
import dev.nstv.lazylayoutmap.ui.grid.griditem.CustomGridItem
import dev.nstv.lazylayoutmap.ui.grid.griditem.DEFAULT_GRID_ITEM_SIZE
import dev.nstv.lazylayoutmap.ui.grid.griditem.ITEMS_PER_ROW
import dev.nstv.lazylayoutmap.ui.grid.griditem.ITEM_INCREASE_FACTOR
import dev.nstv.lazylayoutmap.ui.grid.griditem.MAX_ZOOM_LEVEL
import dev.nstv.lazylayoutmap.ui.grid.griditem.MIN_ZOOM_LEVEL
import dev.nstv.lazylayoutmap.ui.grid.griditem.rememberGridItemsWithZoomAdjustedColors


@Composable
fun LazyGridScreenScrollZoom(
    modifier: Modifier = Modifier,
    minZoomLevel: Float = MIN_ZOOM_LEVEL,
    maxZoomLevel: Float = MAX_ZOOM_LEVEL,
    showItemText: Boolean = true,
    showDebugInfo: Boolean = true,
    useSheep: Boolean = SHEEP,
    toggleFullScreen: () -> Unit = {},
) {
    val density = LocalDensity.current

    // Camera
    var scale by remember { mutableFloatStateOf(1f) }  // 1f == 100%
    var pan by remember { mutableStateOf(Offset.Zero) }
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }

    // Items
    val defaultItemSize = with(density) { DEFAULT_GRID_ITEM_SIZE.toPx() }
    var itemsPerRow by remember { mutableIntStateOf(ITEMS_PER_ROW) }
    val items: List<CustomGridItem> = rememberGridItemsWithZoomAdjustedColors(itemsPerRow)
    val itemProvider = remember(items) {
        LazyGridItemProvider(
            items = items,
            showText = showItemText,
            useSheep = useSheep,
            zoomLevel = { scale },
        )
    }


    // Helper functions
    fun restartCamera() {
        scale = 1f
        pan = Offset.Zero
    }

    fun visibleByZoom(item: CustomGridItem, zoomLevel: Float): Boolean =
        zoomLevel in item.minZoomLevel..item.maxZoomLevel

    Box(modifier.fillMaxSize()) {

        LazyLayout(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            toggleFullScreen()
                        },
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
        if (showDebugInfo) {
            // Overlay window (fixed on top)
            var overlayExpanded by remember { mutableStateOf(true) }
            AnimatedContent(
                targetState = overlayExpanded,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { isExpanded ->
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(
                            Color.Black.copy(alpha = 0.7f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .clickable {
                            overlayExpanded = !overlayExpanded
                        }
                ) {
                    if (isExpanded) {
                        Text(
                            "Viewport: width=${viewportSize.width}, height=${viewportSize.height}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Zoom: ${"%.3f".format(scale)}x ( ${"%.0f".format(scale * 100)}%)",
                            color = Color.White,
                        )
                        Text(
                            "Pan px: x=${"%.1f".format(pan.x)}, y=${"%.1f".format(pan.y)}",
                            color = Color.White
                        )
                    } else {
                        Text(" i ", color = Color.White)
                    }
                }
            }
        }

    }
}