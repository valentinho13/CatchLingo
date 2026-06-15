package de.valentinho13.catchlingo.feature.discover

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ObjectTargetSelectionTest {
    @Test
    fun selectsMostCentralObjectOverLargerOffCenterObject() {
        val result = selectObjectDetectionTarget(
            candidates = listOf(
                box(left = 440, top = 340, right = 560, bottom = 460),
                box(left = 20, top = 20, right = 420, bottom = 360),
            ),
            frameSize = frame,
        )

        assertNotNull(result.selected)
        val selected = result.selected!!
        assertEquals(ObjectTargetSelectionReason.Center, selected.reason)
        assertTrue(selected.centerDistance < 0.05f)
        assertEquals(0.018f, selected.areaRatio, 0.001f)
    }

    @Test
    fun selectsLargerObjectWhenCenterDistanceIsSimilar() {
        val result = selectObjectDetectionTarget(
            candidates = listOf(
                box(left = 450, top = 350, right = 550, bottom = 450),
                box(left = 430, top = 330, right = 610, bottom = 510),
            ),
            frameSize = frame,
        )

        assertNotNull(result.selected)
        val selected = result.selected!!
        assertEquals(ObjectTargetSelectionReason.Size, selected.reason)
        assertEquals(0.0405f, selected.areaRatio, 0.001f)
    }

    @Test
    fun ignoresTinyBoxes() {
        val result = selectObjectDetectionTarget(
            candidates = listOf(
                box(left = 495, top = 395, right = 505, bottom = 405),
                box(left = 40, top = 40, right = 70, bottom = 70),
                box(left = 900, top = 700, right = 920, bottom = 720),
            ),
            frameSize = frame,
        )

        assertEquals(3, result.objectCount)
        assertNull(result.selected)
        assertEquals(ObjectTargetSelectionReason.None, result.selectionReason)
    }

    @Test
    fun returnsNoneWhenNoValidObjectsExist() {
        val result = selectObjectDetectionTarget(
            candidates = emptyList(),
            frameSize = frame,
        )

        assertEquals(0, result.objectCount)
        assertNull(result.selected)
        assertEquals(ObjectTargetSelectionReason.None, result.selectionReason)
    }

    private fun box(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        hasCategoryLabels: Boolean = false,
    ): ObjectDetectionCandidateBox = ObjectDetectionCandidateBox(
        left = left,
        top = top,
        right = right,
        bottom = bottom,
        hasCategoryLabels = hasCategoryLabels,
    )

    private companion object {
        val frame = ObjectDetectionFrameSize(width = 1_000, height = 800)
    }
}
