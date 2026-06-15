package de.valentinho13.catchlingo.feature.discover

internal data class CropBounds(
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int,
)

internal data class LabelComparisonSummary(
    val topWholeFrameLabel: String?,
    val topCropLabel: String?,
    val didCropChangeTopLabel: Boolean,
)

internal data class CropLabelingDiagnostics(
    val cropSuccess: Boolean,
    val cropFailureReason: String?,
    val wholeFrameLabels: List<MlLabelObservation>,
    val cropLabels: List<MlLabelObservation>,
    val labelComparison: LabelComparisonSummary,
)

internal fun calculateCropBounds(
    selectedBox: NormalizedObjectBox?,
    frameWidth: Int,
    frameHeight: Int,
): Result<CropBounds> {
    if (selectedBox == null) {
        return Result.failure(IllegalArgumentException(CROP_FAILURE_NO_TARGET))
    }
    if (frameWidth <= 0 || frameHeight <= 0) {
        return Result.failure(IllegalArgumentException(CROP_FAILURE_INVALID_FRAME))
    }

    val left = (selectedBox.left * frameWidth).toInt().coerceIn(0, frameWidth)
    val top = (selectedBox.top * frameHeight).toInt().coerceIn(0, frameHeight)
    val right = (selectedBox.right * frameWidth).toInt().coerceIn(0, frameWidth)
    val bottom = (selectedBox.bottom * frameHeight).toInt().coerceIn(0, frameHeight)
    val width = right - left
    val height = bottom - top
    if (width <= 0 || height <= 0) {
        return Result.failure(IllegalArgumentException(CROP_FAILURE_INVALID_BOUNDS))
    }

    val areaRatio = (width.toFloat() * height.toFloat()) / (frameWidth.toFloat() * frameHeight.toFloat())
    if (width < MinCropEdgePixels || height < MinCropEdgePixels || areaRatio < MinCropAreaRatio) {
        return Result.failure(IllegalArgumentException(CROP_FAILURE_TINY_CROP))
    }

    return Result.success(CropBounds(left = left, top = top, width = width, height = height))
}

internal fun compareWholeFrameAndCropLabels(
    wholeFrameLabels: List<MlLabelObservation>,
    cropLabels: List<MlLabelObservation>,
): LabelComparisonSummary {
    val topWhole = wholeFrameLabels.topLabelText()
    val topCrop = cropLabels.topLabelText()
    return LabelComparisonSummary(
        topWholeFrameLabel = topWhole,
        topCropLabel = topCrop,
        didCropChangeTopLabel = topWhole != null && topCrop != null && !topWhole.equals(topCrop, ignoreCase = true),
    )
}

internal fun buildSkippedCropLabelingDiagnostics(
    reason: String,
    wholeFrameLabels: List<MlLabelObservation>,
): CropLabelingDiagnostics = CropLabelingDiagnostics(
    cropSuccess = false,
    cropFailureReason = reason,
    wholeFrameLabels = wholeFrameLabels.take(MaxCropDiagnosticLabels),
    cropLabels = emptyList(),
    labelComparison = compareWholeFrameAndCropLabels(wholeFrameLabels, emptyList()),
)

internal fun buildSuccessfulCropLabelingDiagnostics(
    wholeFrameLabels: List<MlLabelObservation>,
    cropLabels: List<MlLabelObservation>,
): CropLabelingDiagnostics = CropLabelingDiagnostics(
    cropSuccess = true,
    cropFailureReason = null,
    wholeFrameLabels = wholeFrameLabels.take(MaxCropDiagnosticLabels),
    cropLabels = cropLabels.take(MaxCropDiagnosticLabels),
    labelComparison = compareWholeFrameAndCropLabels(wholeFrameLabels, cropLabels),
)

private fun List<MlLabelObservation>.topLabelText(): String? =
    maxByOrNull { it.confidence }?.text

internal const val CROP_FAILURE_NO_TARGET = "no-target"
internal const val CROP_FAILURE_INVALID_FRAME = "invalid-frame"
internal const val CROP_FAILURE_INVALID_BOUNDS = "invalid-bounds"
internal const val CROP_FAILURE_TINY_CROP = "tiny-crop"
internal const val CROP_FAILURE_BITMAP_CONVERSION = "bitmap-conversion"
internal const val CROP_FAILURE_LABELING_FAILED = "labeling-failed"
internal const val MaxCropDiagnosticLabels = 5
private const val MinCropEdgePixels = 16
private const val MinCropAreaRatio = 0.01f
