package de.valentinho13.catchlingo.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class CatchLingoSpacing(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 40.dp,
)

@Immutable
data class CatchLingoRadii(
    val button: Dp = 18.dp,
    val chip: Dp = 50.dp,
    val card: Dp = 24.dp,
    val panel: Dp = 28.dp,
    val hero: Dp = 34.dp,
)

@Immutable
data class CatchLingoElevation(
    val quiet: Dp = 2.dp,
    val card: Dp = 8.dp,
    val hero: Dp = 16.dp,
)

data class CatchLingoDesignTokens(
    val spacing: CatchLingoSpacing = CatchLingoSpacing(),
    val radii: CatchLingoRadii = CatchLingoRadii(),
    val elevation: CatchLingoElevation = CatchLingoElevation(),
)

val LocalCatchLingoTokens = staticCompositionLocalOf { CatchLingoDesignTokens() }

val MaterialTheme.catchLingo: CatchLingoDesignTokens
    @Composable get() = LocalCatchLingoTokens.current

private val LightColorScheme: ColorScheme = lightColorScheme(
    primary = CatchLingoColor.Green,
    onPrimary = CatchLingoColor.WarmSurfaceRaised,
    primaryContainer = CatchLingoColor.GreenSoft,
    onPrimaryContainer = CatchLingoColor.GreenDeep,
    secondary = CatchLingoColor.Amber,
    onSecondary = CatchLingoColor.TextPrimary,
    secondaryContainer = CatchLingoColor.AmberSoft,
    onSecondaryContainer = CatchLingoColor.AmberDeep,
    tertiary = CatchLingoColor.Leaf,
    background = CatchLingoColor.Canvas,
    onBackground = CatchLingoColor.TextPrimary,
    surface = CatchLingoColor.WarmSurface,
    onSurface = CatchLingoColor.TextPrimary,
    surfaceVariant = CatchLingoColor.GreenMist,
    onSurfaceVariant = CatchLingoColor.TextMuted,
    outline = CatchLingoColor.Hairline,
    outlineVariant = CatchLingoColor.Hairline,
    error = CatchLingoColor.Coral,
)

private val CatchLingoTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 48.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    ),
)

private val CatchLingoShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(34.dp),
)

@Composable
fun CatchLingoTheme(content: @Composable () -> Unit) {
    androidx.compose.runtime.CompositionLocalProvider(
        LocalCatchLingoTokens provides CatchLingoDesignTokens(),
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = CatchLingoTypography,
            shapes = CatchLingoShapes,
            content = content,
        )
    }
}

fun Color.withPressedAlpha(): Color = copy(alpha = 0.86f)
