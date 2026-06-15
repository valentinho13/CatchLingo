package de.valentinho13.catchlingo.feature.discover

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoveryCandidateRankingTest {
    @Test
    fun bowlAndContainerRanksMangkukAboveWastafel() {
        val ranked = rank(
            MlLabelObservation("Bowl", 0.70f),
            MlLabelObservation("Container", 0.66f),
            MlLabelObservation("Sink", 0.63f),
        )

        assertEquals(DetectionContext.Kitchen, ranked.context)
        assertEquals("mangkuk", ranked.candidates.first().match.id)
        assertTrue(ranked.candidates.first().requiresConfirmation)
        assertTrue(ranked.candidate("mangkuk").score > ranked.candidate("wastafel").score)
    }

    @Test
    fun sinkAloneDoesNotAutoAcceptWastafel() {
        val ranked = rank(MlLabelObservation("Sink", 0.95f))
        val sink = ranked.candidate("wastafel")

        assertTrue(sink.requiresConfirmation)
        assertFalse(sink.canAutoCatch())
    }

    @Test
    fun sinkAndFaucetSuggestWastafelConfirmation() {
        val ranked = rank(
            MlLabelObservation("Sink", 0.88f),
            MlLabelObservation("Faucet", 0.74f),
        )
        val sink = ranked.candidate("wastafel")

        assertTrue(sink.requiresConfirmation)
        assertTrue("Faucet" in sink.supportingLabels)
        assertFalse(sink.canAutoCatch())
    }

    @Test
    fun chairWithMetalAndMusicalInstrumentRequiresConfirmation() {
        val ranked = rank(
            MlLabelObservation("Chair", 0.91f),
            MlLabelObservation("Metal", 0.82f),
            MlLabelObservation("Musical instrument", 0.78f),
        )
        val chair = ranked.candidate("kursi")

        assertTrue(chair.requiresConfirmation)
        assertTrue(chair.riskPenalty > 0f)
        assertFalse(chair.canAutoCatch())
    }

    @Test
    fun kitchenContextBoostsKitchenCandidates() {
        val ranked = rank(
            MlLabelObservation("Kitchen", 0.80f),
            MlLabelObservation("Cup", 0.68f),
        )
        val cup = ranked.candidate("cangkir")

        assertEquals(DetectionContext.Kitchen, ranked.context)
        assertTrue(cup.contextBoost > 0f)
        assertTrue(cup.requiresConfirmation)
    }

    @Test
    fun broadLabelsDoNotDirectlyMapToWords() {
        listOf("food", "kitchen", "tableware", "product").forEach { label ->
            assertEquals(null, mapLabelToVocabulary(label))
        }
    }

    @Test
    fun broadKitchenEvidenceCanSuggestButNotAutoCatch() {
        val ranked = rank(
            MlLabelObservation("Tableware", 0.78f),
            MlLabelObservation("Dish", 0.67f),
        )
        val suggestion = ranked.candidates.firstOrNull()

        assertNotNull(suggestion)
        assertTrue(requireNotNull(suggestion).requiresConfirmation)
        assertFalse(suggestion.canAutoCatch())
    }

    private fun rank(vararg labels: MlLabelObservation): RankedDiscoveryCandidates {
        val directCandidates = labels.mapNotNull { label ->
            mapLabelToVocabulary(label.text)?.let { match ->
                DiscoveryCandidate(labelText = label.text, confidence = label.confidence, match = match)
            }
        }
        return rankDiscoveryCandidates(labels = labels.toList(), directCandidates = directCandidates)
    }

    private fun RankedDiscoveryCandidates.candidate(id: String): DiscoveryCandidate =
        requireNotNull(candidates.firstOrNull { it.match.id == id }) {
            "Expected candidate $id in ${candidates.map { it.match.id }}"
        }
}
