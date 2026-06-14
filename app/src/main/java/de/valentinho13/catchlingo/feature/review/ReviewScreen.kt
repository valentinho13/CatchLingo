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
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.R
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.components.CatchLingoButton
import de.valentinho13.catchlingo.designsystem.components.CatchLingoButtonStyle
import de.valentinho13.catchlingo.designsystem.components.CatchLingoCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoHeroCard
import de.valentinho13.catchlingo.designsystem.components.MiniPill
import de.valentinho13.catchlingo.designsystem.components.StaggeredEntrance
import de.valentinho13.catchlingo.designsystem.components.catchLingoTactileClickable
import de.valentinho13.catchlingo.designsystem.rememberCatchLingoHaptics

@Composable
fun ReviewScreen(
    modifier: Modifier = Modifier,
    onFeedback: (String) -> Unit = {},
) {
    val reviewWords = emptyList<String>()
    var selectedMode by rememberSaveable { mutableStateOf(ReviewMode.Easy) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        if (reviewWords.isEmpty()) {
            StaggeredEntrance(index = 0) {
                ReviewEmptyState()
            }
            StaggeredEntrance(index = 1) {
                CatchLingoCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(text = "Sanftes Erinnern", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Sobald du echte Wörter gesammelt hast, kannst du sie hier ruhig wiederholen.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CatchLingoColor.TextMuted,
                        )
                    }
                }
            }
        } else {
            StaggeredEntrance(index = 0) {
                ReviewPracticeCard(
                    selectedMode = selectedMode,
                    word = reviewWords.first(),
                    onFeedback = onFeedback,
                )
            }
            StaggeredEntrance(index = 1) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ModeCard(
                        mode = ReviewMode.Easy,
                        selected = selectedMode == ReviewMode.Easy,
                        icon = Icons.Outlined.Image,
                        onClick = { selectedMode = ReviewMode.Easy },
                        modifier = Modifier.weight(1f),
                    )
                    ModeCard(
                        mode = ReviewMode.Hard,
                        selected = selectedMode == ReviewMode.Hard,
                        icon = Icons.Outlined.QuestionMark,
                        onClick = { selectedMode = ReviewMode.Hard },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            StaggeredEntrance(index = 2) {
                CatchLingoCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Sanftes Erinnern", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Review bleibt eine ruhige Hilfe für echte Funde, kein Schulmodus und kein Drucksystem.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CatchLingoColor.TextMuted,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    CatchLingoButton(
                        text = "Später erinnern",
                        onClick = {
                            onFeedback("Alles gut. CatchLingo erinnert dich später sanft daran.")
                        },
                        style = CatchLingoButtonStyle.Quiet,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewEmptyState() {
    CatchLingoHeroCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MiniPill(
                text = "Noch nichts zu wiederholen",
                color = CatchLingoColor.GreenSoft,
                contentColor = CatchLingoColor.GreenDeep,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Erst entdecken, dann erinnern",
                style = MaterialTheme.typography.headlineMedium,
                color = CatchLingoColor.GreenDeep,
            )
            Text(
                text = "Review wird aktiv, sobald dein Wörterbuch echte Funde enthält.",
                style = MaterialTheme.typography.bodyMedium,
                color = CatchLingoColor.TextMuted,
            )
            Spacer(modifier = Modifier.height(18.dp))
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(168.dp)
                        .clip(CircleShape)
                        .background(CatchLingoColor.GreenSoft.copy(alpha = 0.72f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = null,
                        tint = CatchLingoColor.Green,
                        modifier = Modifier.size(52.dp),
                    )
                }
                Image(
                    painter = painterResource(R.drawable.welcome_cat),
                    contentDescription = null,
                    modifier = Modifier.size(76.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}

@Composable
private fun ReviewPracticeCard(
    selectedMode: ReviewMode,
    word: String,
    onFeedback: (String) -> Unit,
) {
    CatchLingoHeroCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MiniPill(
                text = selectedMode.title,
                color = selectedMode.softColor(),
                contentColor = selectedMode.accentColor(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = selectedMode.headline,
                style = MaterialTheme.typography.headlineMedium,
                color = selectedMode.accentColor(),
            )
            Text(text = selectedMode.subtitle, style = MaterialTheme.typography.bodyMedium, color = CatchLingoColor.TextMuted)
            Spacer(modifier = Modifier.height(14.dp))
            ReviewPrompt(mode = selectedMode, word = word)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = selectedMode.helperText,
                style = MaterialTheme.typography.bodyMedium,
                color = CatchLingoColor.TextPrimary,
            )
            Spacer(modifier = Modifier.height(18.dp))
            CatchLingoButton(
                text = "Starten",
                onClick = {
                    onFeedback("${selectedMode.title} ist bereit.")
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ReviewPrompt(mode: ReviewMode, word: String) {
    Box(contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .size(168.dp)
                .clip(CircleShape)
                .background(mode.softColor().copy(alpha = 0.62f)),
            contentAlignment = Alignment.Center,
        ) {
            when (mode) {
                ReviewMode.Easy -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = mode.accentColor(),
                        modifier = Modifier.size(46.dp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bild",
                        style = MaterialTheme.typography.titleMedium,
                        color = mode.accentColor(),
                    )
                }

                ReviewMode.Hard -> Text(
                    text = word,
                    style = MaterialTheme.typography.headlineLarge,
                    color = mode.accentColor(),
                )
            }
        }
        Image(
            painter = painterResource(R.drawable.welcome_cat),
            contentDescription = null,
            modifier = Modifier.size(76.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun ModeCard(
    mode: ReviewMode,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = rememberCatchLingoHaptics()
    CatchLingoCard(
        modifier = modifier.catchLingoTactileClickable {
            haptics.softTick()
            onClick()
        },
        elevated = selected,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = mode.accentColor(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = mode.shortTitle, style = MaterialTheme.typography.titleMedium)
            Text(text = mode.shortSubtitle, style = MaterialTheme.typography.labelMedium, color = CatchLingoColor.TextMuted)
            if (selected) {
                Spacer(modifier = Modifier.height(10.dp))
                MiniPill(
                    text = "aktiv",
                    color = mode.softColor(),
                    contentColor = mode.accentColor(),
                )
            }
        }
    }
}

private fun ReviewMode.accentColor() = when (this) {
    ReviewMode.Easy -> CatchLingoColor.Green
    ReviewMode.Hard -> CatchLingoColor.Amber
}

private fun ReviewMode.softColor() = when (this) {
    ReviewMode.Easy -> CatchLingoColor.GreenSoft
    ReviewMode.Hard -> CatchLingoColor.AmberSoft
}

private enum class ReviewMode(
    val title: String,
    val headline: String,
    val subtitle: String,
    val helperText: String,
    val shortTitle: String,
    val shortSubtitle: String,
) {
    Easy(
        title = "Easy Mode",
        headline = "Bild hilft dir",
        subtitle = "Mit visueller Erinnerung",
        helperText = "Sieh das Bild und erinnere dich entspannt an das Wort.",
        shortTitle = "Easy",
        shortSubtitle = "Mit Bild",
    ),
    Hard(
        title = "Hard Mode",
        headline = "Nur das Wort",
        subtitle = "Ruhig, ohne Zusatzhilfe",
        helperText = "Nimm dir einen Moment und prüfe, ob das Wort schon vertraut ist.",
        shortTitle = "Hard",
        shortSubtitle = "Nur Wort",
    ),
}
