package de.valentinho13.catchlingo.feature.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.components.CatchLingoCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoChip

@Composable
fun DictionaryScreen(modifier: Modifier = Modifier) {
    val words = listOf(
        DictionaryWord("kopi", "coffee", "Essen & Trinken", "Neu"),
        DictionaryWord("meja", "table", "Zuhause", "Neu"),
        DictionaryWord("jalan", "street", "Unterwegs", "Bekannt"),
        DictionaryWord("kursi", "chair", "Zuhause", "Lerne"),
        DictionaryWord("sepeda", "bicycle", "Unterwegs", "Bekannt"),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        CatchLingoCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "24 Wörter gesammelt", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Ein ruhiges Feldjournal deiner echten Funde.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CatchLingoColor.TextMuted,
                    )
                }
                Icon(imageVector = Icons.Outlined.Search, contentDescription = null, tint = CatchLingoColor.Green)
                Spacer(modifier = Modifier.size(14.dp))
                Icon(imageVector = Icons.Outlined.FilterList, contentDescription = null, tint = CatchLingoColor.TextMuted)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CatchLingoChip(text = "Alle", selected = true, onClick = {})
            CatchLingoChip(text = "Neu", selected = false, onClick = {})
            CatchLingoChip(text = "Lerne", selected = false, onClick = {})
            CatchLingoChip(text = "Bekannt", selected = false, onClick = {})
        }

        words.forEach { word ->
            DictionaryRow(word = word)
        }
    }
}

@Composable
private fun DictionaryRow(word: DictionaryWord) {
    CatchLingoCard(modifier = Modifier.fillMaxWidth(), elevated = false) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            WordIllustration(word.translation.first().uppercase())
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
            CatchLingoChip(text = word.state, selected = word.state == "Neu", onClick = {})
        }
    }
}

@Composable
private fun WordIllustration(letter: String) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(54.dp)
            .padding(2.dp),
        contentAlignment = Alignment.Center,
    ) {
        CatchLingoCard(
            elevated = false,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            modifier = Modifier.size(54.dp),
        ) {
            androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = letter, style = MaterialTheme.typography.titleLarge, color = CatchLingoColor.AmberDeep)
            }
        }
    }
}

private data class DictionaryWord(
    val translation: String,
    val source: String,
    val category: String,
    val state: String,
)
