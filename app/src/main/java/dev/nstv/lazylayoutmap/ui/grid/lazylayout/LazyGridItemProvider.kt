package dev.nstv.lazylayoutmap.ui.grid.lazylayout

import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import dev.nstv.lazylayoutmap.ui.grid.griditem.CustomGridItem
import dev.nstv.lazylayoutmap.ui.grid.griditem.GridItemView

class LazyGridItemProvider(
    private val items: List<CustomGridItem>,
) : LazyLayoutItemProvider {
    override val itemCount: Int
        get() = items.size

    override fun getKey(index: Int) = items[index].id

    @Composable
    override fun Item(index: Int, key: Any) {
        GridItemView(items[index])
    }
}