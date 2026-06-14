package de.valentinho13.catchlingo.feature.dictionary

import androidx.compose.animation.core.animateIntAsState
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
import androidx.compose.material.icons.automirrored.outlined.DirectionsBike
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Signpost
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.data.DiscoveredWord
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.components.CatchLingoCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoChip
import de.valentinho13.catchlingo.designsystem.components.MiniPill
import de.valentinho13.catchlingo.designsystem.components.StaggeredEntrance
import de.valentinho13.catchlingo.designsystem.rememberCatchLingoHaptics

@Composable
fun DictionaryScreen(
    modifier: Modifier = Modifier,
    words: List<DiscoveredWord> = emptyList(),
    onFeedback: (String) -> Unit = {},
) {
    val filters = listOf("Alle", "Neu", "Lerne", "Bekannt")
    var selectedFilter by rememberSaveable { mutableStateOf("Alle") }
    val visibleWords = if (selectedFilter == "Alle") words else words.filter { it.dictionaryState() == selectedFilter }
    val animatedWordCount by animateIntAsState(targetValue = words.size, label = "dictionaryWordCount")
    val haptics = rememberCatchLingoHaptics()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        StaggeredEntrance(index = 0) {
            CatchLingoCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "$animatedWordCount Wörter gesammelt", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Deine echten Funde erscheinen hier, sobald du die Welt erkundest.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CatchLingoColor.TextMuted,
                        )
                    }
                    if (words.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            haptics.softTick()
                            onFeedback("Suche wird nützlich, sobald erste Wörter gesammelt sind.")
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Wörterbuch durchsuchen",
                            tint = CatchLingoColor.Green,
                        )
                    }
                    IconButton(
                        onClick = {
                            haptics.softTick()
                            onFeedback("Filter erscheinen, sobald es echte Wörter gibt.")
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Wörterbuch filtern",
                            tint = CatchLingoColor.TextMuted,
                        )
                    }
                    }
                }
            }
        }

        if (words.isEmpty()) {
            StaggeredEntrance(index = 1) {
                DictionaryEmptyState()
            }
        } else {
            StaggeredEntrance(index = 1) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    filters.forEach { filter ->
                        CatchLingoChip(
                            text = filter,
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                        )
                    }
                }
            }

            visibleWords.forEachIndexed { index, word ->
                StaggeredEntrance(index = index + 2) {
                    DictionaryRow(word = word)
                }
            }
        }
    }
}

@Composable
private fun DictionaryEmptyState() {
    CatchLingoCard(modifier = Modifier.fillMaxWidth(), elevated = false) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = CatchLingoColor.GreenSoft,
                contentColor = CatchLingoColor.GreenDeep,
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
                Text(text = "Noch keine echten Funde", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Starte Entdecken und sammle dein erstes Wort aus deiner Umgebung.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CatchLingoColor.TextMuted,
                )
            }
        }
    }
}

@Composable
private fun DictionaryRow(word: DiscoveredWord) {
    CatchLingoCard(modifier = Modifier.fillMaxWidth(), elevated = false) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CategoryIllustration(visual = word.category.visual())
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            ) {
                Text(text = word.word, style = MaterialTheme.typography.titleMedium)
                Text(text = word.source, style = MaterialTheme.typography.bodyMedium, color = CatchLingoColor.TextMuted)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = word.category, style = MaterialTheme.typography.labelMedium, color = CatchLingoColor.Green)
            }
            val state = word.dictionaryState()
            MiniPill(
                text = state,
                color = if (state == "Neu") CatchLingoColor.AmberSoft else CatchLingoColor.GreenSoft,
                contentColor = if (state == "Neu") CatchLingoColor.AmberDeep else CatchLingoColor.GreenDeep,
            )
        }
    }
}

@Composable
private fun CategoryIllustration(visual: CategoryVisual) {
    Surface(
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color = visual.background,
        contentColor = visual.tint,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = visual.icon, contentDescription = null)
        }
    }
}

private fun String.visual(): CategoryVisual = when (this) {
    "Essen & Trinken" -> CategoryVisual(
        icon = Icons.Outlined.LocalCafe,
        background = CatchLingoColor.AmberSoft,
        tint = CatchLingoColor.AmberDeep,
    )

    "Zuhause" -> CategoryVisual(
        icon = Icons.Outlined.Chair,
        background = CatchLingoColor.WarmSurface,
        tint = CatchLingoColor.GreenDeep,
    )

    "Unterwegs" -> CategoryVisual(
        icon = Icons.AutoMirrored.Outlined.DirectionsBike,
        background = CatchLingoColor.GreenSoft,
        tint = CatchLingoColor.Green,
    )

    else -> CategoryVisual(
        icon = Icons.Outlined.Signpost,
        background = CatchLingoColor.WarmSurface,
        tint = CatchLingoColor.TextMuted,
    )
}

private data class CategoryVisual(
    val icon: ImageVector,
    val background: Color,
    val tint: Color,
)

private fun DiscoveredWord.dictionaryState(): String = "Neu"
