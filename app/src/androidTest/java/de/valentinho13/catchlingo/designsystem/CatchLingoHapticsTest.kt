package de.valentinho13.catchlingo.designsystem

import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented checks for the catchHold() branch dispatch.
 *
 * These run against the real device/emulator vibrator, so which branch fires
 * depends on the hardware: on API 31+ with full primitive support the
 * composition branch runs, on budget hardware and API 30 the waveform
 * fallback runs, and without any vibrator the View fallback runs. In every
 * case the call must not throw and the fallback must fire exactly when the
 * device has no usable vibrator.
 */
@RunWith(AndroidJUnit4::class)
class CatchLingoHapticsTest {

    @Test
    fun catchHold_withoutVibrator_invokesFallbackExactlyOnce() {
        var fallbackCount = 0
        val haptics = CatchLingoHaptics(vibrator = null, fallback = { fallbackCount++ })

        haptics.catchHold()

        assertEquals(1, fallbackCount)
    }

    @Test
    fun softTick_withoutVibrator_invokesFallbackExactlyOnce() {
        var fallbackCount = 0
        val haptics = CatchLingoHaptics(vibrator = null, fallback = { fallbackCount++ })

        haptics.softTick()

        assertEquals(1, fallbackCount)
    }

    @Test
    fun catchHold_withDeviceVibrator_doesNotThrowAndOnlyFallsBackWithoutHardware() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Vibrator::class.java)
        }

        var fallbackCount = 0
        val haptics = CatchLingoHaptics(vibrator = vibrator, fallback = { fallbackCount++ })

        haptics.catchHold()

        val expectedFallbacks = if (vibrator?.hasVibrator() == true) 0 else 1
        assertEquals(
            "catchHold must vibrate on devices with a vibrator and fall back otherwise",
            expectedFallbacks,
            fallbackCount,
        )
    }
}
