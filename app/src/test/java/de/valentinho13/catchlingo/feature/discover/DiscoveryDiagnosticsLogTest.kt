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

    private fun sampleEvent(
        timestampMillis: Long = 42L,
        decision: DiscoveryDiagnosticDecision = DiscoveryDiagnosticDecision.AutoAccepted,
        selectedCandidateId: String? = null,
        finalCandidateId: String? = "ponsel",
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
    )
}
