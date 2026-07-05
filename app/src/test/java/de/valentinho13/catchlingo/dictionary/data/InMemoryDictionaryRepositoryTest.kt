package de.valentinho13.catchlingo.dictionary.data

import de.valentinho13.catchlingo.core.text.LabelNormalizer
import de.valentinho13.catchlingo.core.time.FakeTimeProvider
import de.valentinho13.catchlingo.dictionary.model.CatchStatus
import de.valentinho13.catchlingo.discover.model.Candidate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryDictionaryRepositoryTest {

    private val time = FakeTimeProvider(now = 1_000L)
    private val repo = InMemoryDictionaryRepository(time)

    private fun candidate(raw: String, confidence: Float = 0.9f) = Candidate(
        id = "c-$raw-${time.now}",
        rawLabel = raw,
        normalizedLabel = LabelNormalizer.normalize(raw),
        confidence = confidence,
        source = "test",
        detectedAt = time.now,
    )

    // Pflichttest 1: Duplicate vs. Wiederbegegnung.
    @Test
    fun confirmingSameCandidateTwice_keepsSingleWord_butRecordsTwoConfirmedEvents() {
        repo.confirm(candidate("Cup"), targetLanguage = "es")
        time.now = 5_000L
        repo.confirm(candidate("Cup"), targetLanguage = "es")

        assertEquals(1, repo.dictionary().size)
        val confirmed = repo.catchEvents().filter { it.status == CatchStatus.CONFIRMED }
        assertEquals(2, confirmed.size)
        // Review bleibt erklärbar/stabil: genau ein fälliges Wort.
        assertEquals(1, repo.reviewDue(now = 5_000L).size)
    }

    // Pflichttest 2: Reject / "Nicht dabei".
    @Test
    fun rejecting_createsNoWord_butRecordsRejectedEvent() {
        repo.reject(candidate("Cup"))

        assertTrue(repo.dictionary().isEmpty())
        val events = repo.catchEvents()
        assertEquals(1, events.size)
        assertEquals(CatchStatus.REJECTED, events.first().status)
        assertEquals(null, events.first().resolvedWordId)
        assertTrue(repo.reviewDue(now = 10_000L).isEmpty())
    }

    @Test
    fun freshRepository_isEmpty() {
        assertTrue(repo.dictionary().isEmpty())
        assertTrue(repo.reviewDue(now = time.now).isEmpty())
        assertTrue(repo.catchEvents().isEmpty())
    }

    @Test
    fun confirmedWord_isImmediatelyReviewDue_andCarriesCreatedAtFromTimeProvider() {
        time.now = 2_500L
        val word = repo.confirm(candidate("Chair"), targetLanguage = "es")

        assertEquals(2_500L, word.createdAt)
        assertEquals(null, word.translation) // keine erfundene Übersetzung
        assertEquals(listOf(word.id), repo.reviewDue(now = 2_500L).map { it.id })
    }

    @Test
    fun normalizationDeduplicatesLabelVariants() {
        repo.confirm(candidate("Cup"), targetLanguage = "es")
        repo.confirm(candidate("  cup "), targetLanguage = "es")

        assertEquals(1, repo.dictionary().size)
        assertEquals("cup", repo.dictionary().first().label)
    }

    @Test
    fun sameLabelDifferentTargetLanguage_createsTwoWords() {
        repo.confirm(candidate("Cup"), targetLanguage = "es")
        repo.confirm(candidate("Cup"), targetLanguage = "fr")

        assertEquals(2, repo.dictionary().size)
    }

    // Wiederbegegnung ist als zusätzlicher CatchEvent sichtbar, verschiebt das Scheduling aber NICHT.
    @Test
    fun reEncounter_addsCatchEvent_butDoesNotReschedule() {
        val word = repo.confirm(candidate("Plant"), targetLanguage = "es") // createdAt = 1_000
        time.now = 9_000L
        repo.confirm(candidate("Plant"), targetLanguage = "es")

        val encounters = repo.catchEvents().count {
            it.status == CatchStatus.CONFIRMED && it.resolvedWordId == word.id
        }
        assertEquals(2, encounters)
        // dueAt blieb bei createdAt (1_000): auch eine Abfrage bei 1_000 findet das Wort noch.
        assertEquals(listOf(word.id), repo.reviewDue(now = 1_000L).map { it.id })
    }
}
