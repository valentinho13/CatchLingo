package de.valentinho13.catchlingo.feature.profile

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.R
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.components.CatchLingoCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoHeroCard

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CatchLingoHeroCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Stevie", style = MaterialTheme.typography.headlineLarge, color = CatchLingoColor.GreenDeep)
                    Text(
                        text = "Dein warmer Begleiter fuer echte Welt-Woerter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CatchLingoColor.TextMuted,
                    )
                }
                Image(
                    painter = painterResource(R.drawable.welcome_cat),
                    contentDescription = null,
                    modifier = Modifier.size(126.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        SettingRow(Icons.Outlined.Language, "Sprachen", "Deutsch -> Indonesisch")
        SettingRow(Icons.Outlined.Vibration, "Haptik", "Premium Catch-Motion aktiviert")
        SettingRow(Icons.Outlined.Palette, "Design", "Warm, ruhig, organisch")
        SettingRow(Icons.Outlined.TouchApp, "Bedienung", "Magnet-Geste als ruhiger Fangmoment")
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
) {
    CatchLingoCard(modifier = Modifier.fillMaxWidth(), elevated = false) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = CatchLingoColor.Green, modifier = Modifier.size(30.dp))
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = CatchLingoColor.TextMuted)
            }
        }
    }
}
