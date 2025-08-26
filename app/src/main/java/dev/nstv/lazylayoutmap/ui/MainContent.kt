package dev.nstv.lazylayoutmap.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import dev.nstv.composablesheep.library.util.SheepColor
import dev.nstv.lazylayoutmap.ui.Screen.GRID_LAZY_SCROLL
import dev.nstv.lazylayoutmap.ui.Screen.GRID_LAZY_SCROLL_ZOOM
import dev.nstv.lazylayoutmap.ui.Screen.GRID_LAZY_SIMPLE
import dev.nstv.lazylayoutmap.ui.Screen.GRID_LAZY_SIMPLE_SCROLL
import dev.nstv.lazylayoutmap.ui.Screen.GRID_NOT_LAZY
import dev.nstv.lazylayoutmap.ui.Screen.GRID_NOT_LAZY_SCROLL
import dev.nstv.lazylayoutmap.ui.Screen.GRID_NOT_LAZY_SCROLL_BOUND
import dev.nstv.lazylayoutmap.ui.Screen.MAP
import dev.nstv.lazylayoutmap.ui.grid.lazylayout.LazyGridScreenRealScroll
import dev.nstv.lazylayoutmap.ui.grid.lazylayout.LazyGridScreenScrollZoom
import dev.nstv.lazylayoutmap.ui.grid.lazylayout.LazyGridScreenSimple
import dev.nstv.lazylayoutmap.ui.grid.lazylayout.LazyGridScreenSimpleScroll
import dev.nstv.lazylayoutmap.ui.grid.nonlazy.NonLazyGridScreen
import dev.nstv.lazylayoutmap.ui.grid.nonlazy.NonLazyGridScreenWithScroll
import dev.nstv.lazylayoutmap.ui.map.LazyMapScreen
import dev.nstv.lazylayoutmap.ui.theme.Grid
import dev.nstv.lazylayoutmap.ui.theme.components.DropDownWithArrows

private const val SHOW_BORDER = true

private enum class Screen {
    MAP,
    GRID_NOT_LAZY,
    GRID_NOT_LAZY_SCROLL,
    GRID_NOT_LAZY_SCROLL_BOUND,
    GRID_LAZY_SIMPLE,
    GRID_LAZY_SIMPLE_SCROLL,
    GRID_LAZY_SCROLL,
    GRID_LAZY_SCROLL_ZOOM,
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) { contentPadding ->
        var selectedScreen by remember { mutableStateOf(GRID_LAZY_SCROLL_ZOOM) }

        val extraModifier = if (SHOW_BORDER) {
            Modifier
                .padding(Grid.Three)
                .border(width = Grid.Half, color = SheepColor.Purple)
        } else Modifier

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .zIndex(2f)
            ) {
                DropDownWithArrows(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopStart),
                    options = Screen.entries.map { it.name }.toList(),
                    selectedIndex = Screen.entries.indexOf(selectedScreen),
                    onSelectionChanged = {
                        selectedScreen = Screen.entries.toTypedArray()[it]
                    },
                    textStyle = MaterialTheme.typography.headlineSmall,
                    loopSelection = true,
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = Grid.One),
                )
            }
            Crossfade(
                targetState = selectedScreen,
                animationSpec = tween(durationMillis = 500)
            ) { screen ->
                when (screen) {
                    MAP -> LazyMapScreen()
                    GRID_NOT_LAZY -> NonLazyGridScreen(extraModifier)
                    GRID_NOT_LAZY_SCROLL -> NonLazyGridScreenWithScroll(extraModifier, false)
                    GRID_NOT_LAZY_SCROLL_BOUND -> NonLazyGridScreenWithScroll(extraModifier)
                    GRID_LAZY_SIMPLE -> LazyGridScreenSimple(extraModifier)
                    GRID_LAZY_SIMPLE_SCROLL -> LazyGridScreenSimpleScroll(extraModifier)
                    GRID_LAZY_SCROLL -> LazyGridScreenRealScroll(extraModifier)
                    GRID_LAZY_SCROLL_ZOOM -> LazyGridScreenScrollZoom(extraModifier)
                }
            }
        }
    }
}