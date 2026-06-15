package de.valentinho13.catchlingo.feature.discover

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DiscoveryVocabularyTest {
    @Test
    fun mapsEverydayObjectsToCuratedWords() {
        assertVocabulary("mobile phone", "ponsel", "phone")
        assertVocabulary("book", "buku", "book")
        assertVocabulary("bottle", "botol", "bottle")
        assertVocabulary("chair", "kursi", "chair")
        assertVocabulary("plant", "tanaman", "plant")
        assertVocabulary("shoe", "sepatu", "shoe")
        assertVocabulary("dog", "anjing", "dog")
    }

    @Test
    fun prefersSpecificObjectLabelsBeforeBroaderLabels() {
        assertVocabulary("coffee cup", "cangkir", "cup")
        assertVocabulary("notebook computer", "laptop", "laptop")
        assertVocabulary("computer keyboard", "keyboard", "keyboard")
        assertVocabulary("computer mouse", "mouse", "computer mouse")
        assertVocabulary("traffic light", "lampu lalu lintas", "traffic light")
    }

    @Test
    fun leavesBroadUnsafeLabelsUnmapped() {
        assertNull(mapLabelToVocabulary("person"))
        assertNull(mapLabelToVocabulary("room"))
        assertNull(mapLabelToVocabulary("vehicle"))
        assertNull(mapLabelToVocabulary("food"))
        assertNull(mapLabelToVocabulary("remote control"))
        assertNull(mapLabelToVocabulary("remote"))
        assertNull(mapLabelToVocabulary("tripod"))
        assertNull(mapLabelToVocabulary("stand"))
        assertNull(mapLabelToVocabulary("telephone"))
        assertNull(mapLabelToVocabulary("hot dog"))
    }

    @Test
    fun riskyLabelsUseHigherConfidenceThresholds() {
        assertMinConfidence("chair", 0.74f)
        assertMinConfidence("mobile phone", 0.78f)
        assertMinConfidence("dog", 0.86f)
    }

    private fun assertVocabulary(
        label: String,
        expectedWord: String,
        expectedSource: String,
    ) {
        val match = requireNotNull(mapLabelToVocabulary(label))
        assertEquals(expectedWord, match.word)
        assertEquals(expectedSource, match.source)
    }

    private fun assertMinConfidence(label: String, expectedMinConfidence: Float) {
        val match = requireNotNull(mapLabelToVocabulary(label))
        assertEquals(expectedMinConfidence, match.minConfidence, 0.001f)
    }
}
