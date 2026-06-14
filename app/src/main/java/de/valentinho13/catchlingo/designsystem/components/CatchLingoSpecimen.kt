package de.valentinho13.catchlingo.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.designsystem.CatchLingoColor

@Composable
fun CatchLingoSpecimenCard(
    word: String,
    source: String,
    modifier: Modifier = Modifier,
    context: String? = null,
    status: String? = null,
) {
    CatchLingoCard(
        modifier = modifier,
        contentPadding = PaddingValues(18.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = word, style = MaterialTheme.typography.titleLarge, color = CatchLingoColor.TextPrimary)
                    Text(text = source, style = MaterialTheme.typography.bodyMedium, color = CatchLingoColor.TextMuted)
                }
                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = CircleShape,
                    color = CatchLingoColor.GreenSoft,
                    contentColor = CatchLingoColor.Green,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
            if (context != null || status != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (context != null) {
                        MiniPill(text = context, color = CatchLingoColor.GreenSoft, contentColor = CatchLingoColor.GreenDeep)
                    }
                    if (status != null) {
                        MiniPill(text = status, color = CatchLingoColor.AmberSoft, contentColor = CatchLingoColor.AmberDeep)
                    }
                }
            }
        }
    }
}

@Composable
fun MiniPill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CatchLingoColor.WarmSurface,
    contentColor: Color = CatchLingoColor.TextMuted,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = color,
        contentColor = contentColor,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}
