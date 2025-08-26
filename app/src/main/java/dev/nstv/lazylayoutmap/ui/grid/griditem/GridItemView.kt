package dev.nstv.lazylayoutmap.ui.grid.griditem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.dp


@Composable
fun GridItemView(
    item: CustomGridItem,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(DEFAULT_GRID_ITEM_SIZE)
            .background(color = item.color, shape = MaterialTheme.shapes.small)
            .innerShadow(
                shape = MaterialTheme.shapes.small,
                shadow = Shadow(radius = 4.dp)
            )
            .border(width = 1.dp, color = item.borderColor, shape = MaterialTheme.shapes.small),
        contentAlignment = Alignment.Center,
    ) {
        Text(item.id)
    }

}