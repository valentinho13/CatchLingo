package de.valentinho13.catchlingo.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.CatchLingoMotion
import de.valentinho13.catchlingo.designsystem.catchLingo

@Composable
fun CatchLingoChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val container by animateColorAsState(
        targetValue = if (selected) CatchLingoColor.Green else CatchLingoColor.WarmSurfaceRaised,
        animationSpec = tween(CatchLingoMotion.Chip, easing = CatchLingoMotion.EaseOutSoft),
        label = "chipContainer",
    )
    val content by animateColorAsState(
        targetValue = if (selected) CatchLingoColor.WarmSurfaceRaised else CatchLingoColor.TextMuted,
        animationSpec = tween(CatchLingoMotion.Chip, easing = CatchLingoMotion.EaseOutSoft),
        label = "chipContent",
    )

    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier.heightIn(min = 38.dp),
        shape = RoundedCornerShape(MaterialTheme.catchLingo.radii.chip),
        border = BorderStroke(1.dp, if (selected) CatchLingoColor.Green else CatchLingoColor.Hairline),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = container,
            containerColor = container,
            selectedLabelColor = content,
            labelColor = content,
            selectedLeadingIconColor = content,
            iconColor = content,
        ),
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 2.dp),
            ) {
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null)
                }
                Text(text = text, style = MaterialTheme.typography.labelMedium)
            }
        },
    )
}
