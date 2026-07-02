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
        runSafely {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator?.hasVibrator() == true) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else {
                fallback()
            }
        }
    }

    fun catchHold() {
        runSafely {
            val vibrator = vibrator
            when {
                vibrator == null || !vibrator.hasVibrator() -> fallback()

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    vibrator.areAllPrimitivesSupported(
                        VibrationEffect.Composition.PRIMITIVE_QUICK_RISE,
                        VibrationEffect.Composition.PRIMITIVE_SPIN,
                        VibrationEffect.Composition.PRIMITIVE_THUD,
                    ) -> {
                    val effect = VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_QUICK_RISE, 0.45f)
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SPIN, 0.35f, 45)
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 0.65f, 80)
                        .compose()
                    vibrator.vibrate(effect)
                }

                // Devices without full primitive support (budget hardware on S+,
                // and everything on R) get a plain waveform: the platform silently
                // drops compositions containing unsupported primitives, so this
                // branch is the only way the catch moment stays feelable there.
                else -> vibrator.vibrate(
                    VibrationEffect.createWaveform(CATCH_HOLD_FALLBACK_TIMINGS, -1),
                )
            }
        }
    }

    private fun runSafely(block: () -> Unit) {
        try {
            block()
        } catch (_: SecurityException) {
            fallback()
        } catch (_: RuntimeException) {
            fallback()
        }
    }

    private companion object {
        // Off/on pattern approximating the composition's contour:
        // short rise pulse, brief gap, heavier thud pulse (~165 ms total).
        val CATCH_HOLD_FALLBACK_TIMINGS = longArrayOf(0, 30, 45, 90)
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
