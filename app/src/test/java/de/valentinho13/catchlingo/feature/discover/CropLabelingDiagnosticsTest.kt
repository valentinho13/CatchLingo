package de.valentinho13.catchlingo.feature.discover

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CropLabelingDiagnosticsTest {
    @Test
    fun cropBoundsClampToFrame() {
        val result = calculateCropBounds(
            selectedBox = NormalizedObjectBox(left = -0.10f, top = 0.10f, right = 1.20f, bottom = 0.90f),
            frameWidth = 1_000,
            frameHeight = 800,
        )

        val bounds = result.getOrThrow()
        assertEquals(0, bounds.left)
        assertEquals(80, bounds.top)
        assertEquals(1_000, bounds.width)
        assertEquals(640, bounds.height)
    }

    @Test
    fun invalidCropBoundsFailGracefully() {
        val result = calculateCropBounds(
            selectedBox = NormalizedObjectBox(left = 0.60f, top = 0.20f, right = 0.50f, bottom = 0.70f),
            frameWidth = 1_000,
            frameHeight = 800,
        )

        assertTrue(result.isFailure)
        assertEquals(CROP_FAILURE_INVALID_BOUNDS, result.exceptionOrNull()?.message)
    }

    @Test
    fun tinyCropFailsGracefully() {
        val result = calculateCropBounds(
            selectedBox = NormalizedObjectBox(left = 0.50f, top = 0.50f, right = 0.505f, bottom = 0.505f),
            frameWidth = 1_000,
            frameHeight = 800,
        )

        assertTrue(result.isFailure)
        assertEquals(CROP_FAILURE_TINY_CROP, result.exceptionOrNull()?.message)
    }

    @Test
    fun labelComparisonDetectsChangedTopLabel() {
        val comparison = compareWholeFrameAndCropLabels(
            wholeFrameLabels = listOf(
                MlLabelObservation(text = "Sink", confidence = 0.71f),
                MlLabelObservation(text = "Bowl", confidence = 0.62f),
            ),
            cropLabels = listOf(
                MlLabelObservation(text = "Bowl", confidence = 0.82f),
                MlLabelObservation(text = "Tableware", confidence = 0.64f),
            ),
        )

        assertEquals("Sink", comparison.topWholeFrameLabel)
        assertEquals("Bowl", comparison.topCropLabel)
        assertTrue(comparison.didCropChangeTopLabel)
    }

    @Test
    fun labelComparisonDoesNotChangeWhenTopLabelMatchesCaseInsensitively() {
        val comparison = compareWholeFrameAndCropLabels(
            wholeFrameLabels = listOf(MlLabelObservation(text = "Bottle", confidence = 0.80f)),
            cropLabels = listOf(MlLabelObservation(text = "bottle", confidence = 0.91f)),
        )

        assertFalse(comparison.didCropChangeTopLabel)
    }
}
