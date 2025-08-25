package dev.nstv.lazylayoutmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import dev.nstv.lazylayoutmap.ui.map.LazyMap
import dev.nstv.lazylayoutmap.ui.Tile
import dev.nstv.lazylayoutmap.ui.grid.LazyGridScreen
import dev.nstv.lazylayoutmap.ui.map.LazyMapScreen
import dev.nstv.lazylayoutmap.ui.theme.Grid
import dev.nstv.lazylayoutmap.ui.theme.LazyLayoutMapTheme
import dev.nstv.lazylayoutmap.ui.theme.components.DropDownWithArrows

private enum class Screen {
    MAP,
    GRID,
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LazyLayoutMapTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { contentPadding ->
                    var selectedScreen by remember { mutableStateOf(Screen.GRID) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding)
                            .padding(Grid.One)
                    ) {
                        DropDownWithArrows(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.TopStart)
                                .zIndex(2f),
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
                                .padding(vertical = Grid.One)
                                .zIndex(2f),
                        )
                        Crossfade(
                            targetState = selectedScreen,
                            animationSpec = tween(durationMillis = 500)
                        ) { screen ->
                            when (screen) {
                                Screen.MAP -> LazyMapScreen()
                                Screen.GRID -> LazyGridScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

