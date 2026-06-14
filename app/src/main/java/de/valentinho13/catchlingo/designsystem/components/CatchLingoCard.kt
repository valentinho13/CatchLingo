package de.valentinho13.catchlingo.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.catchLingo

@Composable
fun CatchLingoCard(
    modifier: Modifier = Modifier,
    elevated: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(MaterialTheme.catchLingo.radii.card)
    Card(
        modifier = modifier.then(
            if (elevated) {
                Modifier.shadow(
                    elevation = MaterialTheme.catchLingo.elevation.card,
                    shape = shape,
                    ambientColor = CatchLingoColor.Shadow,
                    spotColor = CatchLingoColor.Shadow,
                )
            } else {
                Modifier
            },
        ),
        shape = shape,
        border = BorderStroke(1.dp, CatchLingoColor.Hairline),
        colors = CardDefaults.cardColors(containerColor = CatchLingoColor.WarmSurfaceRaised),
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CatchLingoColor.WarmSurfaceRaised, CatchLingoColor.WarmSurface),
                    ),
                )
                .padding(contentPadding),
        ) {
            content()
        }
    }
}

@Composable
fun CatchLingoHeroCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(MaterialTheme.catchLingo.radii.hero)
    Card(
        modifier = modifier.shadow(
            elevation = MaterialTheme.catchLingo.elevation.hero,
            shape = shape,
            ambientColor = CatchLingoColor.Shadow,
            spotColor = CatchLingoColor.Shadow,
        ),
        shape = shape,
        border = BorderStroke(1.dp, CatchLingoColor.Hairline),
        colors = CardDefaults.cardColors(containerColor = CatchLingoColor.GreenMist),
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            CatchLingoColor.GreenMist,
                            CatchLingoColor.AmberSoft.copy(alpha = 0.48f),
                            CatchLingoColor.WarmSurfaceRaised,
                        ),
                    ),
                )
                .padding(24.dp),
        ) {
            content()
        }
    }
}
