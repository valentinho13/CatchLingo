package de.valentinho13.catchlingo.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.CatchLingoMotion
import de.valentinho13.catchlingo.designsystem.components.CatchLingoBottomBar
import de.valentinho13.catchlingo.designsystem.components.CatchLingoNavItem
import de.valentinho13.catchlingo.designsystem.components.CatchLingoTopBar
import de.valentinho13.catchlingo.feature.dictionary.DictionaryScreen
import de.valentinho13.catchlingo.feature.discover.DiscoverScreen
import de.valentinho13.catchlingo.feature.review.ReviewScreen

@Composable
fun CatchLingoApp() {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var exploreFullScreen by rememberSaveable { mutableStateOf(false) }
    val destinations = CatchLingoDestination.entries
    val selected = destinations[selectedIndex]

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CatchLingoColor.Canvas),
        containerColor = CatchLingoColor.Canvas,
        topBar = {
            if (!exploreFullScreen) {
                CatchLingoTopBar(
                    title = selected.title(),
                    subtitle = selected.subtitle(),
                    actionIcon = Icons.Outlined.Settings,
                )
            }
        },
        bottomBar = {
            if (!exploreFullScreen) {
                CatchLingoBottomBar(
                    items = destinations.map { CatchLingoNavItem(it.label, it.icon) },
                    selectedIndex = selectedIndex,
                    onSelected = {
                        exploreFullScreen = false
                        selectedIndex = it
                    },
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            AnimatedContent(
                targetState = selected,
                transitionSpec = {
                    fadeIn(tween(CatchLingoMotion.Screen)) togetherWith
                        fadeOut(tween(CatchLingoMotion.Screen / 2))
                },
                label = "destinationContent",
            ) { destination ->
                when (destination) {
                    CatchLingoDestination.Discover -> DiscoverScreen(
                        exploreFullScreen = exploreFullScreen,
                        onStartExplore = { exploreFullScreen = true },
                        onLeaveExplore = { exploreFullScreen = false },
                    )
                    CatchLingoDestination.Dictionary -> DictionaryScreen()
                    CatchLingoDestination.Review -> ReviewScreen()
                }
            }
        }
    }
}

private fun CatchLingoDestination.title(): String = when (this) {
    CatchLingoDestination.Discover -> "Guten Morgen"
    CatchLingoDestination.Dictionary -> "Mein Wörterbuch"
    CatchLingoDestination.Review -> "Wiederholen"
}

private fun CatchLingoDestination.subtitle(): String = when (this) {
    CatchLingoDestination.Discover -> "Die Welt wartet schon."
    CatchLingoDestination.Dictionary -> "Deine gesammelten Funde."
    CatchLingoDestination.Review -> "Sanft erinnern, kein Drill."
}
