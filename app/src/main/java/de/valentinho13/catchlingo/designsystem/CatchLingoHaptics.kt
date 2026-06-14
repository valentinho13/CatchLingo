package de.valentinho13.catchlingo.designsystem

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

class CatchLingoHaptics internal constructor(
    private val vibrator: Vibrator?,
    private val fallback: () -> Unit,
) {
    fun softTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator != null) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else {
            fallback()
        }
    }

    fun catchHold() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && vibrator != null) {
            val effect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_QUICK_RISE, 0.45f)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SPIN, 0.35f, 45)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 0.65f, 80)
                .compose()
            vibrator.vibrate(effect)
        } else {
            fallback()
        }
    }
}

@Composable
fun rememberCatchLingoHaptics(): CatchLingoHaptics {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    return remember(context, hapticFeedback) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Vibrator::class.java)
        }
        CatchLingoHaptics(
            vibrator = vibrator,
            fallback = { hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress) },
        )
    }
}
