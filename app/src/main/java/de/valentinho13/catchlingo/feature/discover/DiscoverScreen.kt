package de.valentinho13.catchlingo.feature.discover

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.R
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.components.CatchLingoButton
import de.valentinho13.catchlingo.designsystem.components.CatchLingoCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoHeroCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoChip

@Composable
fun DiscoverScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        CatchLingoHeroCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "12",
                        style = MaterialTheme.typography.displayLarge,
                        color = CatchLingoColor.GreenDeep,
                    )
                    Text(
                        text = "Woerter heute",
                        style = MaterialTheme.typography.titleMedium,
                        color = CatchLingoColor.TextPrimary,
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    CatchLingoButton(
                        text = "Weiter entdecken",
                        icon = Icons.Outlined.Search,
                        onClick = {},
                    )
                }
                Image(
                    painter = painterResource(R.drawable.welcome_cat),
                    contentDescription = "CatchLingo Begleiter",
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        CameraPreviewFoundation()

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CatchLingoChip(text = "Heute", selected = true, onClick = {}, icon = Icons.Outlined.Eco)
            CatchLingoChip(text = "Zu Hause", selected = false, onClick = {})
            CatchLingoChip(text = "Unterwegs", selected = false, onClick = {})
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("12", "heute gefangen", Modifier.weight(1f))
            StatCard("3", "Tage in Folge", Modifier.weight(1f))
            StatCard("5", "Sessions", Modifier.weight(1f))
        }
    }
}

@Composable
private fun CameraPreviewFoundation() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.82f)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(
                Brush.verticalGradient(
                    listOf(
                        CatchLingoColor.GreenDeep,
                        CatchLingoColor.Green,
                        CatchLingoColor.AmberDeep.copy(alpha = 0.78f),
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CatchLingoColor.AmberSoft.copy(alpha = 0.36f),
                            CatchLingoColor.GreenDeep.copy(alpha = 0.10f),
                            CatchLingoColor.GreenDeep.copy(alpha = 0.48f),
                        ),
                        radius = 760f,
                    ),
                ),
        )
        WordSpecimen("kopi", "coffee", Modifier.align(Alignment.CenterStart).padding(start = 28.dp))
        WordSpecimen("kursi", "chair", Modifier.align(Alignment.TopEnd).padding(top = 58.dp, end = 24.dp))
        WordSpecimen("meja", "table", Modifier.align(Alignment.BottomStart).padding(start = 38.dp, bottom = 96.dp))
        CatchFocusOrb(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
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
private fun WordSpecimen(word: String, source: String, modifier: Modifier = Modifier) {
    CatchLingoCard(modifier = modifier, elevated = false) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = word, style = MaterialTheme.typography.titleMedium, color = CatchLingoColor.TextPrimary)
            Text(text = source, style = MaterialTheme.typography.labelMedium, color = CatchLingoColor.TextMuted)
        }
    }
}

@Composable
private fun CatchFocusOrb(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(112.dp)
            .clip(CircleShape)
            .background(CatchLingoColor.WarmSurfaceRaised.copy(alpha = 0.94f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "12", style = MaterialTheme.typography.headlineLarge, color = CatchLingoColor.Green)
            Text(text = "erkannt", style = MaterialTheme.typography.labelMedium, color = CatchLingoColor.TextMuted)
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    CatchLingoCard(modifier = modifier, contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium, color = CatchLingoColor.Green)
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = CatchLingoColor.TextMuted)
        }
    }
}
