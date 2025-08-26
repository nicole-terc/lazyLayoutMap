package dev.nstv.lazylayoutmap.ui.map

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.foundation.lazy.layout.LazyLayoutMeasurePolicy
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max

private const val DEBUG = true

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyMap(
    modifier: Modifier = Modifier,
    minZoomLevel: Float = 1f,
    maxZoomLevel: Float = 10f,
    tiles: List<Tile>,
    backgroundPath: String? = null
) {
    require(minZoomLevel > 0) { "Error: minZoomLevel must be higher than 0" }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cache = remember { mutableStateMapOf<String, ImageBitmap>() }

    // Camera state
    var scale by remember { mutableFloatStateOf(1f) }  // 1f == 100%
    var pan by remember { mutableStateOf(Offset.Zero) }
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }
    val worldTopLeft = remember(pan, scale) { Offset(-pan.x / scale, -pan.y / scale) }

    // Images
    var bgBitmap by remember(backgroundPath) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(backgroundPath) {
        if (!backgroundPath.isNullOrBlank()) {
            bgBitmap = withContext(Dispatchers.IO) {
                runCatching {
                    context.assets.open(backgroundPath).use { ins ->
                        val bytes = ins.readBytes()
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }

    suspend fun loadTile(tile: Tile): ImageBitmap? = withContext(Dispatchers.IO) {
        runCatching {
            if (tile.path.isBlank()) return@runCatching null
            context.assets.open(tile.path).use { ins ->
                val bytes = ins.readBytes()
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            }
        }.getOrNull()
    }

    fun requestTile(tile: Tile) {
        if (!cache.containsKey(tile.path)) coroutineScope.launch {
            loadTile(tile)?.let { cache[tile.path] = it }
        }
    }

    // Camera
    fun restartCamera() {
        scale = 1f
        pan = Offset.Zero
    }

    fun clampPan(panOffset: Offset, zoomLevel: Float, viewportSize: IntSize): Offset {
        val worldWidth = viewportSize.width * zoomLevel
        val worldHeight = viewportSize.height * zoomLevel
        val maxX = worldWidth - viewportSize.width
        val maxY = worldHeight - viewportSize.height
        val x = panOffset.x.coerceIn(-maxX, 0f)
        val y = panOffset.y.coerceIn(-maxY, 0f)
        return Offset(x, y)
    }

    fun visibleByZoom(tile: Tile, zoomLevel: Float): Boolean =
        zoomLevel in tile.zoomLevelStart..tile.zoomLevelEnd

    fun intersectsViewport(
        left: Float,
        top: Float,
        width: Float,
        height: Float,
        viewportWidth: Int,
        viewportHeight: Int
    ): Boolean {
        val right = left + width
        val bottom = top + height
        return right > 0f && bottom > 0f && left < viewportWidth && top < viewportHeight
    }

    // ItemProvider
    val provider = {
        object : LazyLayoutItemProvider {
            override val itemCount: Int get() = tiles.size
            override fun getKey(index: Int): Any = tiles[index].id

            @Composable
            override fun Item(index: Int, key: Any) {
                val tile = tiles[index]
                val bmp = cache[tile.path] ?: run { SideEffect { requestTile(tile) }; null }
                Box(Modifier.fillMaxSize()) {
                    bmp?.let { bitmap ->
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = BitmapPainter(image = bitmap),
                            contentDescription = tile.id,
                        )
                    }
                    if (DEBUG) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Cyan.copy(0.5f))
                        )
                    }
                }
            }
        }
    }

    // Measure Policy
    val measurePolicy = remember(scale, pan, tiles) {
        LazyLayoutMeasurePolicy { constraints ->
            val viewportWidth = constraints.maxWidth
            val viewportHeight = constraints.maxHeight
            viewportSize = IntSize(viewportWidth, viewportHeight)

            val panX = pan.x
            val panY = pan.y

            data class Slot(val index: Int, val x: Int, val y: Int, val width: Int, val height: Int)

            val slotsToPlace = ArrayList<Slot>()

            tiles.forEachIndexed { index, tile ->
                if (!visibleByZoom(tile, scale)) return@forEachIndexed

                // Size at zoomLevelStart is 100% of the size
                val adjustedScale = scale / tile.fullSizeZoomLevel
                val width = tile.size.width * adjustedScale
                val height = tile.size.height * adjustedScale

                // Lazy adjust from center
                val left = (viewportSize.center.x + tile.offset.x) * scale + panX
                val top = (viewportSize.center.y + tile.offset.y) * scale + panY

                if (!intersectsViewport(
                        left,
                        top,
                        width,
                        height,
                        viewportWidth,
                        viewportHeight
                    )
                ) return@forEachIndexed

                slotsToPlace += Slot(
                    index = index,
                    x = left.fastRoundToInt(),
                    y = top.fastRoundToInt(),
                    width = max(1, width.fastRoundToInt()),
                    height = max(1, height.fastRoundToInt())
                )
            }

            val placements = ArrayList<Triple<Placeable, Int, Int>>(slotsToPlace.size)
            for (slot in slotsToPlace) {
                val measurables = compose(slot.index)
                require(measurables.size == 1) { "Each tile item must emit exactly one measurable." }
                val placeable = measurables[0].measure(Constraints.fixed(slot.width, slot.height))
                placements += Triple(placeable, slot.x, slot.y)
            }

            layout(viewportWidth, viewportHeight) {
                for ((placeable, x, y) in placements) placeable.place(x, y)
            }
        }
    }

    // Layout
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput("restartCamera") {
                detectTapGestures(
                    onDoubleTap = {
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

                    val candidatePan = panAfterZoom + panChange
                    scale = newZoomLevel
                    pan = clampPan(candidatePan, newZoomLevel, viewportSize)
                }
            }

    ) {
        // Stretched background
        bgBitmap?.let { backgroundImage ->
            Canvas(Modifier.fillMaxSize()) {
                drawImage(
                    image = backgroundImage,
                    dstOffset = IntOffset(
                        pan.x.fastRoundToInt(),
                        pan.y.fastRoundToInt()
                    ),
                    dstSize = IntSize(
                        (size.width * scale).fastRoundToInt().coerceAtLeast(1),
                        (size.height * scale).fastRoundToInt().coerceAtLeast(1)
                    )
                )
            }
        }

        LazyLayout(
            modifier = Modifier.fillMaxSize(),
            itemProvider = provider,
            prefetchState = null,
            measurePolicy = measurePolicy
        )

        if (DEBUG) {
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
                            "World TL: x=${"%.1f".format(worldTopLeft.x)}, y=${
                                "%.1f".format(
                                    worldTopLeft.y
                                )
                            }",
                            color = Color.White
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
