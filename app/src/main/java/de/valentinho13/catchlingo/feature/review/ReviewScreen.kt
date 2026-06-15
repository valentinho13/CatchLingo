package de.valentinho13.catchlingo.feature.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.R
import de.valentinho13.catchlingo.data.DiscoveredWord
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.components.CatchLingoCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoHeroCard
import de.valentinho13.catchlingo.designsystem.components.MiniPill
import de.valentinho13.catchlingo.designsystem.components.StaggeredEntrance
import de.valentinho13.catchlingo.designsystem.components.catchLingoTactileClickable
import de.valentinho13.catchlingo.designsystem.rememberCatchLingoHaptics

@Composable
fun ReviewScreen(
    modifier: Modifier = Modifier,
    words: List<DiscoveredWord> = emptyList(),
) {
    var revealedWordIds by rememberSaveable { mutableStateOf(emptyList<String>()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (words.isEmpty()) {
            item {
                StaggeredEntrance(index = 0) {
                    ReviewEmptyState()
                }
            }
            item {
                StaggeredEntrance(index = 1) {
                    ReviewWaitingCard()
                }
            }
        } else {
            item {
                StaggeredEntrance(index = 0) {
                    ReviewIntroCard(wordCount = words.size)
                }
            }
            itemsIndexed(words, key = { _, word -> word.id }) { index, word ->
                val revealed = word.id in revealedWordIds
                StaggeredEntrance(index = index + 1) {
                    RecallWordCard(
                        word = word,
                        revealed = revealed,
                        onToggleReveal = {
                            revealedWordIds = if (revealed) {
                                revealedWordIds - word.id
                            } else {
                                revealedWordIds + word.id
                            }
                        },
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
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Review wird aktiv, sobald dein Wörterbuch echte Funde enthält.",
                style = MaterialTheme.typography.bodyMedium,
                color = CatchLingoColor.TextMuted,
                textAlign = TextAlign.Center,
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
private fun ReviewWaitingCard() {
    CatchLingoCard(modifier = Modifier.fillMaxWidth(), elevated = false) {
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

@Composable
private fun ReviewIntroCard(wordCount: Int) {
    CatchLingoCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(54.dp),
                shape = CircleShape,
                color = CatchLingoColor.AmberSoft,
                contentColor = CatchLingoColor.AmberDeep,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.padding(14.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp),
            ) {
                Text(
                    text = "Deine Funde erinnern",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Tippe eine Karte an und prüfe, ob du dich erinnerst.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CatchLingoColor.TextMuted,
                )
                Spacer(modifier = Modifier.height(8.dp))
                MiniPill(
                    text = if (wordCount == 1) "1 echter Fund" else "$wordCount echte Funde",
                    color = CatchLingoColor.GreenSoft,
                    contentColor = CatchLingoColor.GreenDeep,
                )
            }
        }
    }
}

@Composable
private fun RecallWordCard(
    word: DiscoveredWord,
    revealed: Boolean,
    onToggleReveal: () -> Unit,
) {
    val haptics = rememberCatchLingoHaptics()

    CatchLingoCard(
        modifier = Modifier
            .fillMaxWidth()
            .catchLingoTactileClickable {
                haptics.softTick()
                onToggleReveal()
            },
        elevated = revealed,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(58.dp),
                shape = CircleShape,
                color = if (revealed) CatchLingoColor.GreenSoft else CatchLingoColor.WarmSurface,
                contentColor = if (revealed) CatchLingoColor.GreenDeep else CatchLingoColor.AmberDeep,
            ) {
                Icon(
                    imageVector = Icons.Outlined.TouchApp,
                    contentDescription = null,
                    modifier = Modifier.padding(15.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp),
            ) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.headlineSmall,
                    color = CatchLingoColor.GreenDeep,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (revealed) word.source else "Tippen zum Aufdecken",
                    style = if (revealed) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                    color = if (revealed) CatchLingoColor.TextPrimary else CatchLingoColor.TextMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (revealed) "Aus deinem Feldjournal" else "Erinnere dich einen Moment lang.",
                    style = MaterialTheme.typography.labelMedium,
                    color = CatchLingoColor.TextMuted,
                    modifier = Modifier.padding(top = 5.dp),
                )
            }
        }
    }
}
