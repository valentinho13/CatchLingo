package de.valentinho13.catchlingo.feature.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.R
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.components.CatchLingoButton
import de.valentinho13.catchlingo.designsystem.components.CatchLingoButtonStyle
import de.valentinho13.catchlingo.designsystem.components.CatchLingoCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoHeroCard

@Composable
fun ReviewScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        CatchLingoHeroCard(modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Easy Mode", style = MaterialTheme.typography.headlineMedium, color = CatchLingoColor.GreenDeep)
                Text(text = "Bild hilft dir", style = MaterialTheme.typography.bodyMedium, color = CatchLingoColor.TextMuted)
                Spacer(modifier = Modifier.height(14.dp))
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(168.dp)
                            .clip(CircleShape)
                            .background(CatchLingoColor.AmberSoft.copy(alpha = 0.52f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "kopi", style = MaterialTheme.typography.headlineLarge, color = CatchLingoColor.AmberDeep)
                    }
                    Image(
                        painter = painterResource(R.drawable.welcome_cat),
                        contentDescription = null,
                        modifier = Modifier.size(76.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Sieh das Bild und erinnere dich an das Wort.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CatchLingoColor.TextPrimary,
                )
                Spacer(modifier = Modifier.height(18.dp))
                CatchLingoButton(text = "Starten", onClick = {}, modifier = Modifier.fillMaxWidth())
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ModeCard("Easy", "Mit Bild", Icons.Outlined.Image, Modifier.weight(1f))
            ModeCard("Hard", "Nur das Wort", Icons.Outlined.QuestionMark, Modifier.weight(1f))
        }

        CatchLingoCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Sanftes Erinnern", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Review bleibt eine ruhige Hilfe fuer echte Funde, kein Schulmodus und kein Drucksystem.",
                style = MaterialTheme.typography.bodyMedium,
                color = CatchLingoColor.TextMuted,
            )
            Spacer(modifier = Modifier.height(14.dp))
            CatchLingoButton(
                text = "Spaeter erinnern",
                onClick = {},
                style = CatchLingoButtonStyle.Quiet,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
) {
    CatchLingoCard(modifier = modifier, elevated = false) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = CatchLingoColor.Amber)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.labelMedium, color = CatchLingoColor.TextMuted)
        }
    }
}
