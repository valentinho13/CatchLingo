package de.valentinho13.catchlingo.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.CatchLingoMotion
import de.valentinho13.catchlingo.designsystem.catchLingo
import de.valentinho13.catchlingo.designsystem.rememberCatchLingoHaptics

data class CatchLingoNavItem(
    val label: String,
    val icon: ImageVector,
)

@Composable
fun CatchLingoTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionIcon: ImageVector? = null,
    onActionClick: () -> Unit = {},
) {
    val haptics = rememberCatchLingoHaptics()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = CatchLingoColor.TextMuted,
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = CatchLingoColor.TextPrimary,
            )
        }
        if (actionIcon != null) {
            IconButton(
                onClick = {
                    haptics.softTick()
                    onActionClick()
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(CatchLingoColor.WarmSurfaceRaised),
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = null,
                    tint = CatchLingoColor.TextPrimary,
                )
            }
        }
    }
}

@Composable
fun CatchLingoBottomBar(
    items: List<CatchLingoNavItem>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = rememberCatchLingoHaptics()
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(MaterialTheme.catchLingo.radii.panel),
        color = CatchLingoColor.WarmSurfaceRaised,
        shadowElevation = 14.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                val selected = index == selectedIndex
                val tint by animateColorAsState(
                    targetValue = if (selected) CatchLingoColor.Green else CatchLingoColor.TextMuted,
                    animationSpec = tween(CatchLingoMotion.Chip),
                    label = "navTint",
                )
                val indicatorWidth by animateDpAsState(
                    targetValue = if (selected) 22.dp else 4.dp,
                    animationSpec = tween(CatchLingoMotion.Chip, easing = CatchLingoMotion.EaseOutSoft),
                    label = "navIndicatorWidth",
                )

                IconButton(
                    onClick = {
                        if (!selected) {
                            haptics.softTick()
                        }
                        onSelected(index)
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(imageVector = item.icon, contentDescription = item.label, tint = tint)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = tint,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(width = indicatorWidth, height = 3.dp)
                                .clip(CircleShape)
                                .background(if (selected) CatchLingoColor.Green else CatchLingoColor.Hairline),
                        )
                    }
                }
            }
        }
    }
}
