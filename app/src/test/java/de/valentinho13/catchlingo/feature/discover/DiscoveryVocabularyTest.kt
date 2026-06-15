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
        assertNull(mapLabelToVocabulary("vegetable"))
        assertNull(mapLabelToVocabulary("tableware"))
        assertNull(mapLabelToVocabulary("tablecloth"))
        assertNull(mapLabelToVocabulary("glasses"))
        assertNull(mapLabelToVocabulary("sunglasses"))
    }

    @Test
    fun riskyLabelsUseHigherConfidenceThresholds() {
        assertMinConfidence("chair", 0.86f)
        assertMinConfidence("mobile phone", 0.90f)
        assertMinConfidence("dog", 0.86f)
    }

    @Test
    fun riskyLabelsRequireConfidenceAtOrAboveTheirThresholds() {
        assertNotEligible("mobile phone", 0.89f)
        assertEligible("mobile phone", 0.90f)
        assertNotEligible("chair", 0.85f)
        assertEligible("chair", 0.86f)
    }

    @Test
    fun directTableAndDrinkingGlassLabelsRemainMapped() {
        assertVocabulary("table", "meja", "table")
        assertVocabulary("desk", "meja", "table")
        assertVocabulary("glass", "gelas", "glass")
        assertVocabulary("drinking glass", "gelas", "glass")
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

    private fun assertEligible(label: String, confidence: Float) {
        val match = requireNotNull(mapLabelToVocabulary(label))
        assertEquals(true, confidence >= match.minConfidence)
    }

    private fun assertNotEligible(label: String, confidence: Float) {
        val match = requireNotNull(mapLabelToVocabulary(label))
        assertEquals(false, confidence >= match.minConfidence)
    }
}
