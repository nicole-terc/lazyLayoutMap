package dev.nstv.lazylayoutmap.ui.grid.griditem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import dev.nstv.composablesheep.library.ComposableSheep
import dev.nstv.composablesheep.library.model.Sheep
import dev.nstv.composablesheep.library.util.SheepColor
import dev.nstv.lazylayoutmap.ui.SHEEP


@Composable
fun GridItemView(
    item: CustomGridItem,
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    useSheep: Boolean = SHEEP,
    zoomLevel: () -> Float = { MIN_ZOOM_LEVEL },
) {

    val actualZoomLevel by rememberUpdatedState(zoomLevel)
    val itemZoomLevel by remember {
        derivedStateOf {
            actualZoomLevel() / item.fullSizeZoomLevel
        }
    }

    val boxModifier = modifier
        .size(DEFAULT_GRID_ITEM_SIZE) then
            if (!useSheep) {
                Modifier
                    .background(color = item.color, shape = MaterialTheme.shapes.small)
                    .innerShadow(
                        shape = MaterialTheme.shapes.small,
                        shadow = Shadow(radius = 4.dp * itemZoomLevel)
                    )
                    .border(
                        width = 1.dp * itemZoomLevel,
                        color = item.borderColor,
                        shape = MaterialTheme.shapes.small
                    )
            } else Modifier

    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center,
    ) {
        if (useSheep) {
            val sheepAlpha = item.color.alpha
            val sheep = Sheep(
                fluffColor = item.color,
                legColor = SheepColor.Skin.copy(sheepAlpha),
            )
            val sheepBorderColor = item.borderColor.copy(0.3f)
            ComposableSheep(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.45f)
                    .graphicsLayer {
                        rotationY = if (item.index % 2 == 0) 180f else 0f
                    },
                sheep = sheep,
                fluffColor = sheepBorderColor,
                legColor = sheepBorderColor,
                headColor = sheepBorderColor,
                glassesColor = sheepBorderColor,
                eyeColor = sheepBorderColor,
            )
            ComposableSheep(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.4f)
                    .graphicsLayer {
                        rotationY = if (item.index % 2 == 0) 180f else 0f
                    },
                sheep = sheep,
            )
        }
        if (showText) {
            Text(
                item.id,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize * itemZoomLevel,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

}