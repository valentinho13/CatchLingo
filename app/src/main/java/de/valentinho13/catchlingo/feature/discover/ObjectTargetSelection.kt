package de.valentinho13.catchlingo.feature.discover

import java.util.Locale
import kotlin.math.abs
import kotlin.math.hypot

internal data class ObjectDetectionFrameSize(
    val width: Int,
    val height: Int,
)

internal data class ObjectDetectionCandidateBox(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
    val hasCategoryLabels: Boolean,
) {
    val width: Int get() = (right - left).coerceAtLeast(0)
    val height: Int get() = (bottom - top).coerceAtLeast(0)
}

internal enum class ObjectTargetSelectionReason {
    Center,
    Size,
    None,
}

internal data class NormalizedObjectBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    fun compactString(): String =
        "[${left.short()},${top.short()},${right.short()},${bottom.short()}]"
}

internal data class SelectedObjectTarget(
    val box: NormalizedObjectBox,
    val centerDistance: Float,
    val areaRatio: Float,
    val hasCategoryLabels: Boolean,
    val reason: ObjectTargetSelectionReason,
)

internal data class ObjectDetectionDiagnostics(
    val objectCount: Int,
    val frameWidth: Int,
    val frameHeight: Int,
    val selected: SelectedObjectTarget?,
) {
    val selectionReason: ObjectTargetSelectionReason =
        selected?.reason ?: ObjectTargetSelectionReason.None
}

internal fun selectObjectDetectionTarget(
    candidates: List<ObjectDetectionCandidateBox>,
    frameSize: ObjectDetectionFrameSize,
): ObjectDetectionDiagnostics {
    if (frameSize.width <= 0 || frameSize.height <= 0) {
        return ObjectDetectionDiagnostics(
            objectCount = candidates.size,
            frameWidth = frameSize.width,
            frameHeight = frameSize.height,
            selected = null,
        )
    }

    val frameArea = frameSize.width.toFloat() * frameSize.height.toFloat()
    val scored = candidates.mapNotNull { candidate ->
        val areaRatio = (candidate.width.toFloat() * candidate.height.toFloat()) / frameArea
        if (areaRatio < MinObjectAreaRatio || candidate.width <= 0 || candidate.height <= 0) {
            null
        } else {
            val centerX = ((candidate.left + candidate.right) / 2f) / frameSize.width
            val centerY = ((candidate.top + candidate.bottom) / 2f) / frameSize.height
            ScoredObjectDetectionCandidate(
                candidate = candidate,
                centerDistance = normalizedCenterDistance(centerX, centerY),
                areaRatio = areaRatio,
            )
        }
    }

    if (scored.isEmpty()) {
        return ObjectDetectionDiagnostics(
            objectCount = candidates.size,
            frameWidth = frameSize.width,
            frameHeight = frameSize.height,
            selected = null,
        )
    }

    val closestDistance = scored.minOf { it.centerDistance }
    val similarlyCentered = scored.filter {
        it.centerDistance - closestDistance <= SimilarCenterDistanceThreshold
    }
    val selected = similarlyCentered.maxWith(
        compareBy<ScoredObjectDetectionCandidate> { it.areaRatio }
            .thenByDescending { -it.centerDistance },
    )
    val reason = if (selected.centerDistance <= closestDistance + CenterReasonEpsilon) {
        ObjectTargetSelectionReason.Center
    } else {
        ObjectTargetSelectionReason.Size
    }

    return ObjectDetectionDiagnostics(
        objectCount = candidates.size,
        frameWidth = frameSize.width,
        frameHeight = frameSize.height,
        selected = SelectedObjectTarget(
            box = selected.candidate.normalized(frameSize),
            centerDistance = selected.centerDistance,
            areaRatio = selected.areaRatio,
            hasCategoryLabels = selected.candidate.hasCategoryLabels,
            reason = reason,
        ),
    )
}

private data class ScoredObjectDetectionCandidate(
    val candidate: ObjectDetectionCandidateBox,
    val centerDistance: Float,
    val areaRatio: Float,
)

private fun ObjectDetectionCandidateBox.normalized(frameSize: ObjectDetectionFrameSize): NormalizedObjectBox =
    NormalizedObjectBox(
        left = left.toFloat().div(frameSize.width).coerceIn(0f, 1f),
        top = top.toFloat().div(frameSize.height).coerceIn(0f, 1f),
        right = right.toFloat().div(frameSize.width).coerceIn(0f, 1f),
        bottom = bottom.toFloat().div(frameSize.height).coerceIn(0f, 1f),
    )

private fun normalizedCenterDistance(centerX: Float, centerY: Float): Float =
    (hypot(centerX - 0.5f, centerY - 0.5f) / MaxNormalizedCenterDistance).coerceIn(0f, 1f)

private fun Float.short(): String = String.format(Locale.US, "%.2f", this)

private const val MinObjectAreaRatio = 0.01f
private const val SimilarCenterDistanceThreshold = 0.05f
private const val CenterReasonEpsilon = 0.0001f
private const val MaxNormalizedCenterDistance = 0.70710677f
