package dev.nstv.lazylayoutmap.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import dev.nstv.lazylayoutmap.ui.grid.lazylayout.LazyLayoutGridScreen
import dev.nstv.lazylayoutmap.ui.grid.nonlazy.NonLazyGridScreen
import dev.nstv.lazylayoutmap.ui.grid.nonlazy.NonLazyGridScreenWithScroll
import dev.nstv.lazylayoutmap.ui.map.LazyMapScreen
import dev.nstv.lazylayoutmap.ui.theme.Grid
import dev.nstv.lazylayoutmap.ui.theme.components.DropDownWithArrows

private enum class Screen {
    MAP,
    GRID_NOT_LAZY,
    GRID_NOT_LAZY_SCROLL,
    GRID_NOT_LAZY_SCROLL_BOUND,
    GRID_LAZY,
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) { contentPadding ->
        var selectedScreen by remember { mutableStateOf(Screen.GRID_NOT_LAZY_SCROLL) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = Grid.One)
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
                    Screen.MAP -> LazyMapScreen()
                    Screen.GRID_NOT_LAZY -> NonLazyGridScreen()
                    Screen.GRID_NOT_LAZY_SCROLL -> NonLazyGridScreenWithScroll(constrainScroll = false)
                    Screen.GRID_NOT_LAZY_SCROLL_BOUND -> NonLazyGridScreenWithScroll()
                    Screen.GRID_LAZY -> LazyLayoutGridScreen()
                }
            }
        }
    }
}