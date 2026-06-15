package de.valentinho13.catchlingo.feature.discover

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoveryDiagnosticsLogTest {
    @Test
    fun diagnosticsHistoryStoresAnEvent() {
        val history = DiscoveryDiagnosticsHistory().add(sampleEvent())

        assertEquals(1, history.rawEntries().size)
        assertTrue(history.exportJson().contains("\"decision\":\"AutoAccepted\""))
    }

    @Test
    fun diagnosticsHistoryTrimsToMaxSize() {
        val history = (0 until MaxDiscoveryDiagnosticEvents + 5).fold(DiscoveryDiagnosticsHistory()) { current, index ->
            current.add(sampleEvent(timestampMillis = index.toLong()))
        }

        assertEquals(MaxDiscoveryDiagnosticEvents, history.rawEntries().size)
        assertFalse(history.exportJson().contains("\"timestampMillis\":0"))
        assertTrue(history.exportJson().contains("\"timestampMillis\":254"))
    }

    @Test
    fun userConfirmationEventStoresSelectedCandidateId() {
        val event = sampleEvent(
            decision = DiscoveryDiagnosticDecision.UserConfirmed,
            selectedCandidateId = "ponsel",
            finalCandidateId = "ponsel",
        )

        val json = event.toJson()
        assertTrue(json.contains("\"decision\":\"UserConfirmed\""))
        assertTrue(json.contains("\"selectedCandidateId\":\"ponsel\""))
        assertTrue(json.contains("\"finalCandidateId\":\"ponsel\""))
    }

    @Test
    fun noneOfTheseEventStoresNullSelectedCandidateId() {
        val event = sampleEvent(
            decision = DiscoveryDiagnosticDecision.UserRejectedNoneOfThese,
            selectedCandidateId = null,
            finalCandidateId = null,
        )

        val json = event.toJson()
        assertTrue(json.contains("\"decision\":\"UserRejectedNoneOfThese\""))
        assertTrue(json.contains("\"selectedCandidateId\":null"))
    }

    @Test
    fun exportSummaryReportsEventCountAndDecisions() {
        val history = DiscoveryDiagnosticsHistory()
            .add(sampleEvent(timestampMillis = 1L, decision = DiscoveryDiagnosticDecision.AutoAccepted))
            .add(sampleEvent(timestampMillis = 2L, decision = DiscoveryDiagnosticDecision.UserConfirmed))

        val export = history.exportText()
        assertTrue(export.contains("Events: 2"))
        assertTrue(export.contains("- AutoAccepted: 1"))
        assertTrue(export.contains("- UserConfirmed: 1"))
        assertTrue(export.contains("Raw JSON:"))
    }

    @Test
    fun alreadyKnownEventCanBeRepresented() {
        val event = DiscoveryDiagnosticEvent(
            timestampMillis = 9L,
            labels = emptyList(),
            candidates = emptyList(),
            proposedCandidateId = "bunga",
            finalCandidateId = "bunga",
            selectedCandidateId = null,
            decision = DiscoveryDiagnosticDecision.AlreadyKnown,
            reasons = listOf("repositoryDuplicate"),
        )

        val json = event.toJson()
        assertTrue(json.contains("\"decision\":\"AlreadyKnown\""))
        assertTrue(json.contains("\"finalCandidateId\":\"bunga\""))
        assertTrue(json.contains("repositoryDuplicate"))
    }

    @Test
    fun exportSerializationContainsDiagnosticsButNoImageData() {
        val history = DiscoveryDiagnosticsHistory().add(
            sampleEvent(decision = DiscoveryDiagnosticDecision.PendingConfirmation),
        )

        val json = history.exportJson()
        assertTrue(json.contains("\"labels\""))
        assertTrue(json.contains("\"Mobile phone\""))
        assertTrue(json.contains("\"candidates\""))
        assertTrue(json.contains("\"proposedCandidateId\":\"ponsel\""))
        assertTrue(json.contains("\"decision\":\"PendingConfirmation\""))
        assertFalse(json.contains("image"))
        assertFalse(json.contains("photo"))
        assertFalse(json.contains("bitmap"))
    }

    @Test
    fun objectDetectionMetadataIsSerializedAndSummarized() {
        val history = DiscoveryDiagnosticsHistory()
            .add(
                sampleEvent(
                    timestampMillis = 1L,
                    objectDetection = ObjectDetectionDiagnostics(
                        objectCount = 3,
                        frameWidth = 1280,
                        frameHeight = 720,
                        selected = SelectedObjectTarget(
                            box = NormalizedObjectBox(left = 0.25f, top = 0.20f, right = 0.75f, bottom = 0.80f),
                            centerDistance = 0.08f,
                            areaRatio = 0.30f,
                            hasCategoryLabels = true,
                            reason = ObjectTargetSelectionReason.Center,
                        ),
                    ),
                ),
            )
            .add(
                sampleEvent(
                    timestampMillis = 2L,
                    objectDetection = ObjectDetectionDiagnostics(
                        objectCount = 0,
                        frameWidth = 1280,
                        frameHeight = 720,
                        selected = null,
                    ),
                ),
            )

        val json = history.exportJson()
        assertTrue(json.contains("\"objectDetection\""))
        assertTrue(json.contains("\"objectCount\":3"))
        assertTrue(json.contains("\"selectionReason\":\"center\""))
        assertTrue(json.contains("\"hasCategoryLabels\":true"))
        assertTrue(json.contains("\"box\""))

        val text = history.exportText()
        assertTrue(text.contains("Object detection:"))
        assertTrue(text.contains("- selected target exists: 1/2"))
        assertTrue(text.contains("- average selected area ratio: 0.3000"))
        assertTrue(text.contains("  - 0: 1"))
        assertTrue(text.contains("  - 3: 1"))
    }

    @Test
    fun cropLabelingMetadataIsSerializedAndSummarized() {
        val history = DiscoveryDiagnosticsHistory()
            .add(
                sampleEvent(
                    timestampMillis = 1L,
                    cropLabeling = buildSuccessfulCropLabelingDiagnostics(
                        wholeFrameLabels = listOf(MlLabelObservation("Sink", 0.71f)),
                        cropLabels = listOf(MlLabelObservation("Bowl", 0.82f)),
                    ),
                ),
            )
            .add(
                sampleEvent(
                    timestampMillis = 2L,
                    cropLabeling = buildSkippedCropLabelingDiagnostics(
                        reason = CROP_FAILURE_NO_TARGET,
                        wholeFrameLabels = listOf(MlLabelObservation("Room", 0.60f)),
                    ),
                ),
            )

        val json = history.exportJson()
        assertTrue(json.contains("\"cropLabeling\""))
        assertTrue(json.contains("\"wholeFrameLabels\""))
        assertTrue(json.contains("\"cropLabels\""))
        assertTrue(json.contains("\"cropSuccess\":true"))
        assertTrue(json.contains("\"cropFailureReason\":\"no-target\""))
        assertTrue(json.contains("\"topWholeFrameLabel\":\"Sink\""))
        assertTrue(json.contains("\"topCropLabel\":\"Bowl\""))
        assertTrue(json.contains("\"didCropChangeTopLabel\":true"))

        val text = history.exportText()
        assertTrue(text.contains("Crop labeling:"))
        assertTrue(text.contains("- attempts: 2"))
        assertTrue(text.contains("- success rate: 0.50"))
        assertTrue(text.contains("- changed top label: 1/1"))
        assertTrue(text.contains("  - Bowl: 1"))
    }

    @Test
    fun benchmarkExportSummaryReportsRecognitionQualitySignals() {
        val changedCropEvent = sampleEvent(
            timestampMillis = 10L,
            cropLabeling = buildSuccessfulCropLabelingDiagnostics(
                wholeFrameLabels = listOf(MlLabelObservation("Sink", 0.71f)),
                cropLabels = listOf(MlLabelObservation("Bowl", 0.82f)),
            ),
            objectDetection = selectedObjectDiagnostics(),
        )
        val confirmedEvent = sampleEvent(
            timestampMillis = 11L,
            decision = DiscoveryDiagnosticDecision.UserConfirmed,
            selectedCandidateId = "mangkuk",
            finalCandidateId = "mangkuk",
            cropLabeling = buildSuccessfulCropLabelingDiagnostics(
                wholeFrameLabels = listOf(MlLabelObservation("Bowl", 0.76f)),
                cropLabels = listOf(MlLabelObservation("Bowl", 0.88f)),
            ),
        )
        val rejectedEvent = sampleEvent(
            timestampMillis = 12L,
            decision = DiscoveryDiagnosticDecision.UserRejectedNoneOfThese,
            selectedCandidateId = null,
            finalCandidateId = null,
            cropLabeling = buildSkippedCropLabelingDiagnostics(
                reason = CROP_FAILURE_NO_TARGET,
                wholeFrameLabels = listOf(MlLabelObservation("Room", 0.61f)),
            ),
        )
        val alreadyKnownEvent = sampleEvent(
            timestampMillis = 13L,
            decision = DiscoveryDiagnosticDecision.AlreadyKnown,
            finalCandidateId = "ponsel",
            cropLabeling = buildSkippedCropLabelingDiagnostics(
                reason = CROP_FAILURE_NO_TARGET,
                wholeFrameLabels = listOf(MlLabelObservation("Phone", 0.90f)),
            ),
        )
        val noCandidateCropEvent = DiscoveryDiagnosticEvent(
            timestampMillis = 14L,
            labels = listOf(MlLabelObservation("Product", 0.65f)),
            candidates = emptyList(),
            proposedCandidateId = null,
            finalCandidateId = null,
            selectedCandidateId = null,
            decision = DiscoveryDiagnosticDecision.Ignored,
            cropLabeling = buildSuccessfulCropLabelingDiagnostics(
                wholeFrameLabels = listOf(MlLabelObservation("Product", 0.65f)),
                cropLabels = listOf(MlLabelObservation("Bottle", 0.80f)),
            ),
        )
        val history = DiscoveryDiagnosticsHistory()
            .add(changedCropEvent)
            .add(confirmedEvent)
            .add(rejectedEvent)
            .add(alreadyKnownEvent)
            .add(noCandidateCropEvent)

        val export = history.exportText()
        assertTrue(export.contains("Benchmark summary"))
        assertTrue(export.contains("- total events: 5"))
        assertTrue(export.contains("- whole-frame top labels:"))
        assertTrue(export.contains("  - Sink: 1"))
        assertTrue(export.contains("  - Bowl: 1"))
        assertTrue(export.contains("- crop top labels:"))
        assertTrue(export.contains("  - Bowl: 2"))
        assertTrue(export.contains("  - Bottle: 1"))
        assertTrue(export.contains("- object detection targets: 1/5"))
        assertTrue(export.contains("- crop success rate: 3/5 (0.60)"))
        assertTrue(export.contains("- cropTop != wholeFrameTop: 2/3"))
        assertTrue(export.contains("- confirmed words:"))
        assertTrue(export.contains("  - mangkuk: 1"))
        assertTrue(export.contains("- rejected/none-of-these: 1"))
        assertTrue(export.contains("- already-known: 1"))
        assertTrue(export.contains("Benchmark hints"))
        assertTrue(export.contains("- recent cropTop != wholeFrameTop:"))
        assertTrue(export.contains("whole=Sink crop=Bowl"))
        assertTrue(export.contains("- recent no candidate but crop labels existed:"))
        assertTrue(export.contains("whole=Product crop=Bottle"))
        assertTrue(export.contains("- recent user-confirmed candidates:"))
        assertTrue(export.contains("candidate=mangkuk"))

        val rawJson = export.substringAfter("Raw JSON:").trimStart('\r', '\n')
        assertEquals(history.exportJson(), rawJson)
    }

    private fun sampleEvent(
        timestampMillis: Long = 42L,
        decision: DiscoveryDiagnosticDecision = DiscoveryDiagnosticDecision.AutoAccepted,
        selectedCandidateId: String? = null,
        finalCandidateId: String? = "ponsel",
        objectDetection: ObjectDetectionDiagnostics? = null,
        cropLabeling: CropLabelingDiagnostics? = null,
    ): DiscoveryDiagnosticEvent = DiscoveryDiagnosticEvent(
        timestampMillis = timestampMillis,
        labels = listOf(MlLabelObservation(text = "Mobile phone", confidence = 0.92f)),
        candidates = listOf(
            DiscoveryDiagnosticCandidate(
                id = "ponsel",
                word = "ponsel",
                source = "phone",
                category = "Unterwegs",
                labelText = "Mobile phone",
                confidence = 0.92f,
            ),
        ),
        proposedCandidateId = "ponsel",
        finalCandidateId = finalCandidateId,
        selectedCandidateId = selectedCandidateId,
        decision = decision,
        reasons = listOf("RiskyWord"),
        objectDetection = objectDetection,
        cropLabeling = cropLabeling,
    )

    private fun selectedObjectDiagnostics(): ObjectDetectionDiagnostics =
        ObjectDetectionDiagnostics(
            objectCount = 2,
            frameWidth = 1280,
            frameHeight = 720,
            selected = SelectedObjectTarget(
                box = NormalizedObjectBox(left = 0.20f, top = 0.25f, right = 0.80f, bottom = 0.85f),
                centerDistance = 0.04f,
                areaRatio = 0.36f,
                hasCategoryLabels = true,
                reason = ObjectTargetSelectionReason.Center,
            ),
        )
}
