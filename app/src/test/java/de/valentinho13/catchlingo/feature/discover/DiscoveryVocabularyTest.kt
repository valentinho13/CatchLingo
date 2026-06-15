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
}
