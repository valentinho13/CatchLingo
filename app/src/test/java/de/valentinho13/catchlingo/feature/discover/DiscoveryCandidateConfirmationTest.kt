package de.valentinho13.catchlingo.feature.discover

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoveryCandidateConfirmationTest {
    @Test
    fun riskyMappedWordsRequireConfirmation() {
        val phone = requireNotNull(mapLabelToVocabulary("mobile phone"))
        val pending = buildPendingConfirmation(
            originalLabels = listOf(MlLabelObservation("Mobile phone", 0.94f)),
            candidates = listOf(DiscoveryCandidate("Mobile phone", 0.94f, phone)),
            proposedWord = phone,
            acceptedConfidence = 0.94f,
        )

        requireNotNull(pending)
        assertEquals(phone, pending.proposedWord)
        assertTrue(ConfirmationReason.RiskyWord in pending.reasons)
        assertTrue(CandidateConfirmationOption.NoneOfThese in pending.options)
    }

    @Test
    fun sinkRequiresConfirmationEvenAtHighConfidence() {
        val sink = requireNotNull(mapLabelToVocabulary("sink"))
        val pending = buildPendingConfirmation(
            originalLabels = listOf(MlLabelObservation("Sink", 0.95f)),
            candidates = listOf(DiscoveryCandidate("Sink", 0.95f, sink)),
            proposedWord = sink,
            acceptedConfidence = 0.95f,
        )

        requireNotNull(pending)
        assertTrue(ConfirmationReason.RiskyWord in pending.reasons)
    }

    @Test
    fun nonRiskyConfidentMappedWordsCanAutoCatch() {
        val book = requireNotNull(mapLabelToVocabulary("book"))
        val pending = buildPendingConfirmation(
            originalLabels = listOf(MlLabelObservation("Book", 0.91f)),
            candidates = listOf(DiscoveryCandidate("Book", 0.91f, book)),
            proposedWord = book,
            acceptedConfidence = 0.91f,
        )

        assertNull(pending)
    }

    @Test
    fun nearThresholdMappedWordsRequireConfirmation() {
        val bottle = requireNotNull(mapLabelToVocabulary("bottle"))
        val pending = buildPendingConfirmation(
            originalLabels = listOf(MlLabelObservation("Bottle", 0.65f)),
            candidates = listOf(DiscoveryCandidate("Bottle", 0.65f, bottle)),
            proposedWord = bottle,
            acceptedConfidence = 0.65f,
        )

        requireNotNull(pending)
        assertTrue(ConfirmationReason.NearThreshold in pending.reasons)
    }

    @Test
    fun softCandidatePathRequiresConfirmation() {
        val bowl = requireNotNull(mapLabelToSoftVocabulary("soup bowl"))
        val pending = buildDebugPendingConfirmation(
            originalLabels = listOf(MlLabelObservation("Soup bowl", 0.58f)),
            candidates = listOf(DiscoveryCandidate("Soup bowl", 0.58f, bowl)),
        )

        requireNotNull(pending)
        assertTrue(pending.requiresConfirmation)
        assertTrue(ConfirmationReason.SoftCandidate in pending.reasons)
        assertTrue(CandidateConfirmationOption.NoneOfThese in pending.options)
    }

    @Test
    fun correctionHistoryTrimsToOneHundredEvents() {
        val history = (0 until 105).fold(DiscoveryCorrectionHistory()) { current, index ->
            current.add(
                DiscoveryCorrectionEvent(
                    timestampMillis = index.toLong(),
                    labels = listOf(MlLabelObservation("Book", 0.9f)),
                    shownCandidateIds = listOf("buku"),
                    selectedCandidateId = if (index % 2 == 0) "buku" else null,
                ),
            )
        }

        assertEquals(MaxCorrectionHistoryEvents, history.entries().size)
        assertEquals(5L, history.entries().first().timestampMillis)
        assertEquals(104L, history.entries().last().timestampMillis)
    }

    @Test
    fun noneOfTheseCorrectionCanBeRepresented() {
        val event = DiscoveryCorrectionEvent(
            timestampMillis = 7L,
            labels = listOf(MlLabelObservation("Product", 0.82f)),
            shownCandidateIds = listOf("ponsel", "kursi"),
            selectedCandidateId = null,
        )

        assertNull(event.selectedCandidateId)
        assertEquals(listOf("ponsel", "kursi"), event.shownCandidateIds)
    }
}
