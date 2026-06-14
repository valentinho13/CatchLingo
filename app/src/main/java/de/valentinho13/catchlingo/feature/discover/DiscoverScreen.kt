package de.valentinho13.catchlingo.feature.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.R
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.CatchLingoMotion
import de.valentinho13.catchlingo.designsystem.components.CatchLingoButton
import de.valentinho13.catchlingo.designsystem.components.CatchLingoCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoHeroCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoSpecimenCard
import de.valentinho13.catchlingo.designsystem.components.MiniPill
import de.valentinho13.catchlingo.designsystem.rememberCatchLingoHaptics
import kotlin.math.roundToInt

@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    exploreFullScreen: Boolean = false,
    state: DiscoverUiState = EmptyDiscoverState,
    exploreState: DiscoverUiState = PreviewDiscoverState,
    onStartExplore: () -> Unit = {},
    onLeaveExplore: () -> Unit = {},
) {
    if (exploreFullScreen) {
        ExploreScreen(
            state = exploreState,
            onLeaveExplore = onLeaveExplore,
            modifier = modifier,
        )
    } else {
        HomeScreen(
            state = state,
            onStartExplore = onStartExplore,
            modifier = modifier,
        )
    }
}

@Composable
private fun HomeScreen(
    state: DiscoverUiState,
    onStartExplore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CatchLingoColor.Canvas),
        state = rememberLazyListState(),
        contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            HomeHero(state = state, onStartExplore = onStartExplore)
        }
        item {
            FirstFindCard()
        }
        item {
            WarmPreviewCard()
        }
    }
}

@Composable
private fun HomeHero(state: DiscoverUiState, onStartExplore: () -> Unit) {
    CatchLingoHeroCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                MiniPill(
                    text = state.greeting,
                    color = CatchLingoColor.WarmSurfaceRaised.copy(alpha = 0.84f),
                    contentColor = CatchLingoColor.GreenDeep,
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Noch keine Wörter",
                    style = MaterialTheme.typography.headlineLarge,
                    color = CatchLingoColor.GreenDeep,
                )
                Text(
                    text = state.companionLine,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CatchLingoColor.TextMuted,
                    modifier = Modifier.padding(top = 8.dp, end = 8.dp),
                )
                Spacer(modifier = Modifier.height(18.dp))
                CatchLingoButton(
                    text = "Ersten Fund entdecken",
                    icon = Icons.Outlined.Explore,
                    onClick = onStartExplore,
                )
            }
            FloatingCompanion()
        }
    }
}

@Composable
private fun FirstFindCard() {
    CatchLingoCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = CatchLingoColor.GreenSoft,
                contentColor = CatchLingoColor.GreenDeep,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.padding(13.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            ) {
                Text(text = "Dein Wörterbuch wartet", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Sammle dein erstes Wort aus der echten Umgebung. Danach wächst hier dein Feldjournal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CatchLingoColor.TextMuted,
                )
            }
        }
    }
}

@Composable
private fun WarmPreviewCard() {
    CatchLingoCard(modifier = Modifier.fillMaxWidth()) {
        Text(text = "So fühlt sich ein Fund an", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        CatchLingoSpecimenCard(
            word = "kopi",
            source = "coffee",
            context = "Café",
            status = "Beispielfund",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ExploreScreen(
    state: DiscoverUiState,
    onLeaveExplore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CatchLingoColor.Canvas),
    ) {
        SunnyCameraScene(
            state = state,
            modifier = Modifier.fillMaxSize(),
        )
        ExploreChrome(
            state = state,
            onLeaveExplore = onLeaveExplore,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun ExploreChrome(
    state: DiscoverUiState,
    onLeaveExplore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = rememberCatchLingoHaptics()
    Column(
        modifier = modifier
            .statusBarsPadding()
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MiniPill(
                text = "Automatisch sammeln",
                color = CatchLingoColor.GreenDeep.copy(alpha = 0.72f),
                contentColor = CatchLingoColor.WarmSurfaceRaised,
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    haptics.softTick()
                    onLeaveExplore()
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(CatchLingoColor.WarmSurfaceRaised.copy(alpha = 0.88f)),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Explore schließen",
                    tint = CatchLingoColor.TextPrimary,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = state.sceneTitle,
                style = MaterialTheme.typography.labelMedium,
                color = CatchLingoColor.WarmSurfaceRaised.copy(alpha = 0.82f),
            )
            Row(verticalAlignment = Alignment.Bottom) {
                CatchOrb(words = state.wordsToday)
                Spacer(modifier = Modifier.weight(1f))
                MiniPill(
                    text = "schau dich um...",
                    color = CatchLingoColor.WarmSurfaceRaised.copy(alpha = 0.86f),
                    contentColor = CatchLingoColor.TextMuted,
                )
            }
        }
    }
}

@Composable
private fun FloatingCompanion() {
    val transition = rememberInfiniteTransition(label = "catFloat")
    val lift by transition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = CatchLingoMotion.EaseInOutWarm),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "catLift",
    )

    Image(
        painter = painterResource(R.drawable.welcome_cat),
        contentDescription = "CatchLingo Begleiter",
        modifier = Modifier
            .size(148.dp)
            .graphicsLayer {
                translationY = lift
                shadowElevation = 18f
            },
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun SunnyCameraScene(
    state: DiscoverUiState,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "exploreScene")
    val trailPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = CatchLingoMotion.EaseInOutWarm),
            repeatMode = RepeatMode.Restart,
        ),
        label = "trailPhase",
    )

    BoxWithConstraints(
        modifier = modifier
            .background(Color(0xFF6D7D4B)),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawSunnyRoom()
            drawMagnetTrails(words = state.noticedWords, phase = trailPhase)
        }

        state.noticedWords.forEachIndexed { index, word ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(420 + index * 90)) + scaleIn(initialScale = 0.92f),
            ) {
                SceneWordChip(word = word, index = index)
            }
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.SceneWordPosition(
    word: NoticedWord,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    Box(
        modifier = Modifier.offset {
            with(density) {
                IntOffset(
                    x = (maxWidth.toPx() * word.x).roundToInt() - 54.dp.roundToPx(),
                    y = (maxHeight.toPx() * word.y).roundToInt() - 26.dp.roundToPx(),
                )
            }
        },
    ) {
        content()
    }
}

@Composable
private fun BoxWithConstraintsScope.SceneWordChip(word: NoticedWord, index: Int) {
    val transition = rememberInfiniteTransition(label = "wordChip$index")
    val breathe by transition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800 + index * 160, easing = CatchLingoMotion.EaseInOutWarm),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "wordBreathe$index",
    )

    SceneWordPosition(word = word) {
        CatchLingoCard(
            elevated = false,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            modifier = Modifier.graphicsLayer {
                scaleX = breathe
                scaleY = breathe
            },
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = word.word, style = MaterialTheme.typography.titleMedium, color = CatchLingoColor.TextPrimary)
                Text(text = word.source, style = MaterialTheme.typography.labelMedium, color = CatchLingoColor.TextMuted)
            }
        }
    }
}

@Composable
private fun CatchOrb(words: Int, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "catchOrb")
    val pulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = CatchLingoMotion.EaseInOutWarm),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orbPulse",
    )
    Box(
        modifier = modifier
            .size(124.dp)
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            }
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        CatchLingoColor.WarmSurfaceRaised,
                        CatchLingoColor.AmberSoft.copy(alpha = 0.9f),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Outlined.AutoAwesome, contentDescription = null, tint = CatchLingoColor.Amber)
            Text(text = words.toString(), style = MaterialTheme.typography.headlineLarge, color = CatchLingoColor.Green)
            Text(
                text = "Wörter",
                style = MaterialTheme.typography.labelMedium,
                color = CatchLingoColor.TextMuted,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSunnyRoom() {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFDFA3),
                Color(0xFFB9894D),
                Color(0xFF536D43),
                Color(0xFF23423A),
            ),
        ),
    )

    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                CatchLingoColor.AmberSoft.copy(alpha = 0.86f),
                CatchLingoColor.Amber.copy(alpha = 0.24f),
                Color.Transparent,
            ),
            center = Offset(size.width * 0.18f, size.height * 0.16f),
            radius = size.width * 0.86f,
        ),
    )

    drawRect(
        color = Color(0xFF715238).copy(alpha = 0.62f),
        topLeft = Offset(0f, size.height * 0.62f),
        size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.38f),
    )

    repeat(8) { index ->
        val y = size.height * (0.65f + index * 0.043f)
        drawLine(
            color = Color.White.copy(alpha = 0.08f),
            start = Offset(0f, y),
            end = Offset(size.width, y + size.height * 0.035f),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }

    drawRoundRect(
        color = Color(0xFFFFF2C8).copy(alpha = 0.58f),
        topLeft = Offset(size.width * 0.08f, size.height * 0.10f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.26f, size.height * 0.26f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
    )
    repeat(4) { index ->
        val start = Offset(size.width * (0.12f + index * 0.055f), size.height * 0.11f)
        drawLine(
            color = Color.White.copy(alpha = 0.34f),
            start = start,
            end = Offset(start.x + size.width * 0.26f, size.height * 0.72f),
            strokeWidth = 18.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }

    drawRoundRect(
        color = Color(0xFF5E3A24).copy(alpha = 0.92f),
        topLeft = Offset(size.width * 0.18f, size.height * 0.58f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.52f, size.height * 0.10f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
    )
    drawRoundRect(
        color = Color(0xFF4B2C1B).copy(alpha = 0.82f),
        topLeft = Offset(size.width * 0.22f, size.height * 0.68f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.06f, size.height * 0.19f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
    )
    drawRoundRect(
        color = Color(0xFF4B2C1B).copy(alpha = 0.82f),
        topLeft = Offset(size.width * 0.58f, size.height * 0.68f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.06f, size.height * 0.19f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
    )

    drawCircle(
        color = Color(0xFFF6EFE4).copy(alpha = 0.96f),
        radius = size.width * 0.07f,
        center = Offset(size.width * 0.36f, size.height * 0.54f),
    )
    drawOval(
        color = Color(0xFF5B351E).copy(alpha = 0.92f),
        topLeft = Offset(size.width * 0.31f, size.height * 0.51f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.10f, size.height * 0.035f),
    )

    drawCircle(
        color = Color(0xFF254E38).copy(alpha = 0.92f),
        radius = size.width * 0.09f,
        center = Offset(size.width * 0.82f, size.height * 0.50f),
    )
    repeat(5) { index ->
        val angle = -0.8f + index * 0.38f
        drawLine(
            color = Color(0xFF89A95B).copy(alpha = 0.76f),
            start = Offset(size.width * 0.82f, size.height * 0.50f),
            end = Offset(
                size.width * (0.82f + kotlin.math.cos(angle) * 0.13f),
                size.height * (0.50f + kotlin.math.sin(angle) * 0.12f),
            ),
            strokeWidth = 10.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                CatchLingoColor.TextPrimary.copy(alpha = 0.26f),
            ),
            startY = size.height * 0.52f,
            endY = size.height,
        ),
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMagnetTrails(
    words: List<NoticedWord>,
    phase: Float,
) {
    val target = Offset(size.width * 0.5f, size.height * 0.78f)
    words.forEachIndexed { index, word ->
        val start = Offset(size.width * word.x, size.height * word.y)
        val control = Offset(
            x = (start.x + target.x) / 2f,
            y = (start.y + target.y) / 2f - size.height * (0.12f + index * 0.015f),
        )
        val path = Path().apply {
            moveTo(start.x, start.y)
            quadraticTo(control.x, control.y, target.x, target.y)
        }
        drawPath(
            path = path,
            color = CatchLingoColor.Leaf.copy(alpha = 0.34f),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
        )
        val dotProgress = ((phase + index * 0.17f) % 1f)
        val dot = quadraticPoint(start, control, target, dotProgress)
        drawCircle(
            color = CatchLingoColor.AmberSoft.copy(alpha = 0.78f),
            radius = 3.2.dp.toPx(),
            center = dot,
        )
    }
}

private fun quadraticPoint(start: Offset, control: Offset, end: Offset, t: Float): Offset {
    val oneMinus = 1f - t
    return Offset(
        x = oneMinus * oneMinus * start.x + 2f * oneMinus * t * control.x + t * t * end.x,
        y = oneMinus * oneMinus * start.y + 2f * oneMinus * t * control.y + t * t * end.y,
    )
}
