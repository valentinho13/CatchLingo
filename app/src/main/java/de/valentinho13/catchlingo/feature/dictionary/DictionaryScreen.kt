package de.valentinho13.catchlingo.feature.dictionary

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
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.components.CatchLingoCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoChip
import de.valentinho13.catchlingo.designsystem.components.MiniPill
import de.valentinho13.catchlingo.designsystem.rememberCatchLingoHaptics

@Composable
fun DictionaryScreen(
    modifier: Modifier = Modifier,
    onFeedback: (String) -> Unit = {},
) {
    val words = listOf(
        DictionaryWord("kopi", "coffee", "Essen & Trinken", "Neu"),
        DictionaryWord("meja", "table", "Zuhause", "Neu"),
        DictionaryWord("jalan", "street", "Unterwegs", "Bekannt"),
        DictionaryWord("kursi", "chair", "Zuhause", "Lerne"),
        DictionaryWord("sepeda", "bicycle", "Unterwegs", "Bekannt"),
    )
    val filters = listOf("Alle", "Neu", "Lerne", "Bekannt")
    var selectedFilter by rememberSaveable { mutableStateOf("Alle") }
    val visibleWords = if (selectedFilter == "Alle") words else words.filter { it.state == selectedFilter }
    val haptics = rememberCatchLingoHaptics()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        CatchLingoCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "${words.size} Wörter gesammelt", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Ein ruhiges Feldjournal deiner echten Funde.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CatchLingoColor.TextMuted,
                    )
                }
                IconButton(
                    onClick = {
                        haptics.softTick()
                        onFeedback("Suche kommt bald mit ruhiger Wort- und Kontextsuche.")
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
                        onFeedback("Feinere Filter folgen, sobald echte Funde da sind.")
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

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach { filter ->
                CatchLingoChip(
                    text = filter,
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                )
            }
        }

        visibleWords.forEach { word ->
            DictionaryRow(word = word)
        }
    }
}

@Composable
private fun DictionaryRow(word: DictionaryWord) {
    CatchLingoCard(modifier = Modifier.fillMaxWidth(), elevated = false) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CategoryIllustration(visual = word.category.visual())
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            ) {
                Text(text = word.translation, style = MaterialTheme.typography.titleMedium)
                Text(text = word.source, style = MaterialTheme.typography.bodyMedium, color = CatchLingoColor.TextMuted)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = word.category, style = MaterialTheme.typography.labelMedium, color = CatchLingoColor.Green)
            }
            MiniPill(
                text = word.state,
                color = if (word.state == "Neu") CatchLingoColor.AmberSoft else CatchLingoColor.GreenSoft,
                contentColor = if (word.state == "Neu") CatchLingoColor.AmberDeep else CatchLingoColor.GreenDeep,
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

private data class DictionaryWord(
    val translation: String,
    val source: String,
    val category: String,
    val state: String,
)
