package de.valentinho13.catchlingo.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import de.valentinho13.catchlingo.data.DiscoveryRepository
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.CatchLingoMotion
import de.valentinho13.catchlingo.designsystem.components.CatchLingoBottomBar
import de.valentinho13.catchlingo.designsystem.components.CatchLingoNavItem
import de.valentinho13.catchlingo.designsystem.components.CatchLingoTopBar
import de.valentinho13.catchlingo.feature.dictionary.DictionaryScreen
import de.valentinho13.catchlingo.feature.discover.DiscoverScreen
import de.valentinho13.catchlingo.feature.review.ReviewScreen
import java.time.LocalTime
import kotlinx.coroutines.launch

@Composable
fun CatchLingoApp() {
    val context = LocalContext.current
    val discoveryRepository = remember {
        DiscoveryRepository(context.applicationContext)
    }
    val discoveredWords by discoveryRepository.words.collectAsState()
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var exploreFullScreen by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showFeedback: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }
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
                    onActionClick = {
                        showFeedback("Einstellungen kommen bald in einer ruhigen, kleinen Ansicht.")
                    },
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CatchLingoColor.GreenDeep,
                    contentColor = CatchLingoColor.WarmSurfaceRaised,
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
                        onWordCollected = { word ->
                            if (discoveryRepository.collectWord(word)) {
                                showFeedback("Gesammelt: ${word.word}")
                            }
                        },
                        onFeedback = showFeedback,
                    )

                    CatchLingoDestination.Dictionary -> DictionaryScreen(
                        words = discoveredWords,
                        onFeedback = showFeedback,
                    )
                    CatchLingoDestination.Review -> ReviewScreen(onFeedback = showFeedback)
                }
            }
        }
    }
}

private fun CatchLingoDestination.title(): String = when (this) {
    CatchLingoDestination.Discover -> LocalTime.now().catchLingoGreeting()
    CatchLingoDestination.Dictionary -> "Mein Wörterbuch"
    CatchLingoDestination.Review -> "Wiederholen"
}

private fun LocalTime.catchLingoGreeting(): String = when (hour) {
    in 5..10 -> "Guten Morgen"
    in 11..17 -> "Guten Tag"
    else -> "Guten Abend"
}

private fun CatchLingoDestination.subtitle(): String = when (this) {
    CatchLingoDestination.Discover -> "Die Welt wartet schon."
    CatchLingoDestination.Dictionary -> "Deine gesammelten Funde."
    CatchLingoDestination.Review -> "Sanft erinnern, kein Drill."
}
