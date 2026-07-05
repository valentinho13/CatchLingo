package de.valentinho13.catchlingo.feature.dictionary

import de.valentinho13.catchlingo.dictionary.model.Word
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WordDisplayMappingTest {
    @Test
    fun knownVocabularyId_mapsToCuratedDisplayWord() {
        val result = word(label = "cangkir", createdAt = 42L).toDisplayWordOrNull()

        requireNotNull(result)
        assertEquals("cangkir", result.id)
        assertEquals("cangkir", result.word)
        assertEquals("cup", result.source)
        assertEquals("Essen & Trinken", result.category)
    }

    @Test
    fun unknownVocabularyId_isDropped() {
        assertNull(word(label = "not-in-vocabulary").toDisplayWordOrNull())
    }

    @Test
    fun createdAt_mapsToDiscoveredAtMillis() {
        val result = word(label = "kopi", createdAt = 9_876L).toDisplayWordOrNull()

        assertEquals(9_876L, requireNotNull(result).discoveredAtMillis)
    }

    private fun word(label: String, createdAt: Long = 1L) = Word(
        id = 1L,
        label = label,
        targetLanguage = "id",
        translation = null,
        createdAt = createdAt,
    )
}
