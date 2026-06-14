package de.valentinho13.catchlingo.feature.dictionary

import androidx.compose.animation.core.animateIntAsState
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
import androidx.compose.material.icons.automirrored.outlined.DirectionsBike
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Signpost
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.data.DiscoveredWord
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.components.CatchLingoCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoChip
import de.valentinho13.catchlingo.designsystem.components.MiniPill
import de.valentinho13.catchlingo.designsystem.components.StaggeredEntrance
import java.util.concurrent.TimeUnit

@Composable
fun DictionaryScreen(
    modifier: Modifier = Modifier,
    words: List<DiscoveredWord> = emptyList(),
    onFeedback: (String) -> Unit = {},
) {
    val nowMillis = System.currentTimeMillis()
    val filters = listOf("Alle", "Neu").filter { filter ->
        filter == "Alle" || words.any { it.isNew(nowMillis) }
    }
    var selectedFilter by rememberSaveable { mutableStateOf("Alle") }
    val activeFilter = if (selectedFilter in filters) selectedFilter else "Alle"
    val visibleWords = if (activeFilter == "Alle") words else words.filter { it.isNew(nowMillis) }
    val animatedWordCount by animateIntAsState(targetValue = words.size, label = "dictionaryWordCount")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            StaggeredEntrance(index = 0) {
                DictionarySummaryCard(
                    wordCount = animatedWordCount,
                    hasWords = words.isNotEmpty(),
                )
            }
        }

        if (words.isEmpty()) {
            item {
                StaggeredEntrance(index = 1) {
                    DictionaryEmptyState()
                }
            }
        } else {
            item {
                StaggeredEntrance(index = 1) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        filters.forEach { filter ->
                            CatchLingoChip(
                                text = filter,
                                selected = activeFilter == filter,
                                onClick = { selectedFilter = filter },
                            )
                        }
                    }
                }
            }

            itemsIndexed(visibleWords, key = { _, word -> word.id }) { index, word ->
                StaggeredEntrance(index = index + 2) {
                    DictionaryRow(word = word, nowMillis = nowMillis)
                }
            }
        }
    }
}

@Composable
private fun DictionarySummaryCard(
    wordCount: Int,
    hasWords: Boolean,
) {
    CatchLingoCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = if (wordCount == 1) {
                    "1 Wort gesammelt"
                } else {
                    "$wordCount Wörter gesammelt"
                },
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = if (hasWords) {
                    "Deine Sammlung wächst mit jedem echten Fund."
                } else {
                    "Deine echten Funde erscheinen hier, sobald du die Welt erkundest."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = CatchLingoColor.TextMuted,
            )
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
private fun DictionaryRow(word: DiscoveredWord, nowMillis: Long) {
    CatchLingoCard(modifier = Modifier.fillMaxWidth(), elevated = false) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CategoryIllustration(visual = word.visual())
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            ) {
                Text(text = word.word, style = MaterialTheme.typography.titleMedium)
                Text(text = word.source, style = MaterialTheme.typography.bodyMedium, color = CatchLingoColor.TextMuted)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = word.category, style = MaterialTheme.typography.labelMedium, color = CatchLingoColor.Green)
                Text(
                    text = word.relativeCatchTime(nowMillis),
                    style = MaterialTheme.typography.labelMedium,
                    color = CatchLingoColor.TextMuted,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
            if (word.isNew(nowMillis)) {
                MiniPill(
                    text = "Neu",
                    color = CatchLingoColor.AmberSoft,
                    contentColor = CatchLingoColor.AmberDeep,
                )
            }
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

private fun DiscoveredWord.visual(): CategoryVisual = when (id) {
    "ponsel" -> CategoryVisual(
        icon = Icons.Outlined.PhoneAndroid,
        background = CatchLingoColor.GreenSoft,
        tint = CatchLingoColor.Green,
    )

    else -> category.visual()
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

private fun DiscoveredWord.isNew(nowMillis: Long): Boolean =
    nowMillis - discoveredAtMillis < NewWindowMillis

private fun DiscoveredWord.relativeCatchTime(nowMillis: Long): String {
    val ageMillis = (nowMillis - discoveredAtMillis).coerceAtLeast(0L)
    val days = TimeUnit.MILLISECONDS.toDays(ageMillis).toInt()
    return when (days) {
        0 -> "heute gefangen"
        1 -> "gestern gefangen"
        else -> "vor $days Tagen gefangen"
    }
}

private const val NewWindowMillis = 24L * 60L * 60L * 1000L
