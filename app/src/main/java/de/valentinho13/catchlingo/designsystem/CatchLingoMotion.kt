package de.valentinho13.catchlingo.designsystem

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

object CatchLingoMotion {
    const val Micro: Int = 140
    const val Chip: Int = 220
    const val Screen: Int = 340
    const val CatchHold: Int = 1500

    val EaseOutSoft: Easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
    val EaseInOutWarm: Easing = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
}
