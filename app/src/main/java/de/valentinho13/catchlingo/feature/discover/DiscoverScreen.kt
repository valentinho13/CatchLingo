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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import de.valentinho13.catchlingo.designsystem.components.CatchLingoChip
import de.valentinho13.catchlingo.designsystem.components.CatchLingoHeroCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoSpecimenCard
import de.valentinho13.catchlingo.designsystem.components.MiniPill
import kotlin.math.roundToInt

@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    state: DiscoverUiState = PreviewDiscoverState,
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
            DiscoverHero(state = state)
        }
        item {
            WarmDiscoveryScene(state = state)
        }
        item {
            CategoryRail(categories = state.categories)
        }
        item {
            SessionPulseCard(state = state)
        }
        items(state.freshFinds) { word ->
            CatchLingoSpecimenCard(
                word = word.word,
                source = word.source,
                context = word.context,
                status = word.status,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DiscoverHero(state: DiscoverUiState) {
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
                    text = state.wordsToday.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = CatchLingoColor.GreenDeep,
                )
                Text(
                    text = "Woerter heute",
                    style = MaterialTheme.typography.titleMedium,
                    color = CatchLingoColor.TextPrimary,
                )
                Text(
                    text = state.companionLine,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CatchLingoColor.TextMuted,
                    modifier = Modifier.padding(top = 6.dp, end = 8.dp),
                )
                Spacer(modifier = Modifier.height(18.dp))
                CatchLingoButton(
                    text = "Weiter entdecken",
                    icon = Icons.Outlined.Explore,
                    onClick = {},
                )
            }
            FloatingCompanion()
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
private fun WarmDiscoveryScene(state: DiscoverUiState) {
    val transition = rememberInfiniteTransition(label = "discoverScene")
    val pulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = CatchLingoMotion.EaseInOutWarm),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orbPulse",
    )
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
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.78f)
            .clip(MaterialTheme.shapes.extraLarge)
            .drawBehind {
                drawRoundRect(
                    color = CatchLingoColor.Shadow.copy(alpha = 0.22f),
                    topLeft = Offset(0f, 18f),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(34.dp.toPx()),
                )
            }
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF785A2A),
                        Color(0xFF385C39),
                        Color(0xFF173A31),
                    ),
                ),
            ),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CatchLingoColor.AmberSoft.copy(alpha = 0.72f),
                        CatchLingoColor.Amber.copy(alpha = 0.22f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.18f, size.height * 0.12f),
                    radius = size.width * 0.88f,
                ),
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CatchLingoColor.Leaf.copy(alpha = 0.28f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.72f, size.height * 0.42f),
                    radius = size.width * 0.52f,
                ),
            )
            drawWarmSceneLines()
            drawMagnetTrails(words = state.noticedWords, phase = trailPhase)
        }

        MiniPill(
            text = "Automatisch sammeln",
            color = CatchLingoColor.GreenDeep.copy(alpha = 0.72f),
            contentColor = CatchLingoColor.WarmSurfaceRaised,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
        )

        state.noticedWords.forEachIndexed { index, word ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(420 + index * 90)) + scaleIn(initialScale = 0.92f),
            ) {
                SceneWordChip(word = word, index = index)
            }
        }

        CatchOrb(
            words = state.wordsToday,
            pulse = pulse,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 26.dp),
        )

        Text(
            text = state.sceneTitle,
            style = MaterialTheme.typography.labelMedium,
            color = CatchLingoColor.WarmSurfaceRaised.copy(alpha = 0.82f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 24.dp),
        )

        Icon(
            imageVector = Icons.Outlined.CameraAlt,
            contentDescription = null,
            tint = CatchLingoColor.TextPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(18.dp)
                .clip(CircleShape)
                .background(CatchLingoColor.WarmSurfaceRaised.copy(alpha = 0.92f))
                .padding(10.dp),
        )
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
private fun CatchOrb(words: Int, pulse: Float, modifier: Modifier = Modifier) {
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
                text = "Woerter",
                style = MaterialTheme.typography.labelMedium,
                color = CatchLingoColor.TextMuted,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CategoryRail(categories: List<DiscoverCategory>) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        categories.forEachIndexed { index, category ->
            CatchLingoChip(
                text = "${category.label} ${category.count}",
                selected = category.selected,
                onClick = {},
                icon = if (index == 0) Icons.Outlined.Eco else null,
            )
        }
    }
}

@Composable
private fun SessionPulseCard(state: DiscoverUiState) {
    CatchLingoCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.LocalCafe,
                contentDescription = null,
                tint = CatchLingoColor.Amber,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(CatchLingoColor.AmberSoft.copy(alpha = 0.62f))
                    .padding(10.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            ) {
                Text(text = "Gerade gesammelt", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${state.sessionWords} neue Funde - ${state.rememberedWords} wiederbegegnet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CatchLingoColor.TextMuted,
                )
            }
            Text(
                text = "${state.streakDays} Tage",
                style = MaterialTheme.typography.labelLarge,
                color = CatchLingoColor.GreenDeep,
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWarmSceneLines() {
    val sunColor = CatchLingoColor.AmberSoft.copy(alpha = 0.26f)
    repeat(5) { index ->
        val y = size.height * (0.14f + index * 0.13f)
        drawLine(
            color = sunColor,
            start = Offset(size.width * 0.08f, y),
            end = Offset(size.width * 0.92f, y + size.height * 0.08f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
    repeat(4) { index ->
        val x = size.width * (0.16f + index * 0.2f)
        drawLine(
            color = Color.White.copy(alpha = 0.06f),
            start = Offset(x, size.height * 0.08f),
            end = Offset(x + size.width * 0.08f, size.height * 0.88f),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
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
