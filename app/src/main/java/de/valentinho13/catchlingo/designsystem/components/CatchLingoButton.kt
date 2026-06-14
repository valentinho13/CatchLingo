package de.valentinho13.catchlingo.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.CatchLingoMotion
import de.valentinho13.catchlingo.designsystem.catchLingo

enum class CatchLingoButtonStyle {
    Primary,
    Secondary,
    Quiet
}

@Composable
fun CatchLingoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    style: CatchLingoButtonStyle = CatchLingoButtonStyle.Primary,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(CatchLingoMotion.Micro, easing = CatchLingoMotion.EaseOutSoft),
        label = "buttonScale",
    )
    val shape = RoundedCornerShape(MaterialTheme.catchLingo.radii.button)
    val contentPadding = PaddingValues(horizontal = 20.dp, vertical = 13.dp)

    val content: @Composable () -> Unit = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null)
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }

    when (style) {
        CatchLingoButtonStyle.Primary -> Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
                .heightIn(min = 48.dp)
                .scale(scale),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = CatchLingoColor.Green,
                contentColor = CatchLingoColor.WarmSurfaceRaised,
                disabledContainerColor = CatchLingoColor.GreenSoft,
                disabledContentColor = CatchLingoColor.TextMuted,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 5.dp,
                pressedElevation = 1.dp,
            ),
            interactionSource = interactionSource,
            contentPadding = contentPadding,
            content = { content() },
        )

        CatchLingoButtonStyle.Secondary -> Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
                .heightIn(min = 48.dp)
                .scale(scale),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = CatchLingoColor.Amber,
                contentColor = Color.White,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 1.dp,
            ),
            interactionSource = interactionSource,
            contentPadding = contentPadding,
            content = { content() },
        )

        CatchLingoButtonStyle.Quiet -> OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
                .heightIn(min = 48.dp)
                .scale(scale),
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = CatchLingoColor.WarmSurfaceRaised,
                contentColor = CatchLingoColor.GreenDeep,
            ),
            border = BorderStroke(1.dp, CatchLingoColor.Hairline),
            interactionSource = interactionSource,
            contentPadding = contentPadding,
            content = { content() },
        )
    }
}
