package dev.nstv.lazylayoutmap.ui.grid.griditem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.dp


@Composable
fun GridItemView(
    item: CustomGridItem,
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    zoomLevel: () -> Float = { MIN_ZOOM_LEVEL },
) {

    val actualZoomLevel by rememberUpdatedState(zoomLevel)
    val itemZoomLevel by remember {
        derivedStateOf {
            actualZoomLevel() / item.fullSizeZoomLevel
        }
    }

    Box(
        modifier = modifier
            .size(DEFAULT_GRID_ITEM_SIZE)
            .background(color = item.color, shape = MaterialTheme.shapes.small)
            .innerShadow(
                shape = MaterialTheme.shapes.small,
                shadow = Shadow(radius = 4.dp * itemZoomLevel)
            )
            .border(
                width = 1.dp * itemZoomLevel,
                color = item.borderColor,
                shape = MaterialTheme.shapes.small
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (showText) {
            Text(
                item.id,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize * itemZoomLevel
            )
        }
    }

}