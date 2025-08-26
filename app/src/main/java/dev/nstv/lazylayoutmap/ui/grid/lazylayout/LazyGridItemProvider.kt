package dev.nstv.lazylayoutmap.ui.grid.lazylayout

import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import dev.nstv.lazylayoutmap.ui.SHEEP
import dev.nstv.lazylayoutmap.ui.grid.griditem.CustomGridItem
import dev.nstv.lazylayoutmap.ui.grid.griditem.GridItemView
import dev.nstv.lazylayoutmap.ui.grid.griditem.MIN_ZOOM_LEVEL

class LazyGridItemProvider(
    private val items: List<CustomGridItem>,
    private val showText: Boolean = true,
    private val useSheep: Boolean = SHEEP,
    private val zoomLevel: () -> Float = { MIN_ZOOM_LEVEL },
) : LazyLayoutItemProvider {
    override val itemCount: Int
        get() = items.size

    override fun getKey(index: Int) = items[index].id

    @Composable
    override fun Item(index: Int, key: Any) {
        GridItemView(
            item = items[index],
            showText = showText,
            zoomLevel = zoomLevel,
            useSheep = useSheep
        )
    }
}