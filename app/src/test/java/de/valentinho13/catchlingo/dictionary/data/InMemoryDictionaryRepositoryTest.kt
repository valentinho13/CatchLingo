package de.valentinho13.catchlingo.dictionary.data

import de.valentinho13.catchlingo.core.text.LabelNormalizer
import de.valentinho13.catchlingo.core.time.FakeTimeProvider
import de.valentinho13.catchlingo.dictionary.model.CatchStatus
import de.valentinho13.catchlingo.discover.model.Candidate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
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
    fun confirmingSameCandidateTwice_keepsSingleWord_butRecordsTwoConfirmedEvents() = runTest {
        val first = repo.confirm(candidate("Cup"), targetLanguage = "es")
        time.now = 5_000L
        val second = repo.confirm(candidate("Cup"), targetLanguage = "es")

        assertTrue(first.isNew)
        assertTrue(!second.isNew)
        assertEquals(1, repo.observeDictionary().first().size)
        val confirmed = repo.observeCatchEvents().first().filter { it.status == CatchStatus.CONFIRMED }
        assertEquals(2, confirmed.size)
        assertEquals(1, repo.observeReviewDue().first().size)
    }

    // Pflichttest 2: Reject / "Nicht dabei".
    @Test
    fun rejecting_createsNoWord_butRecordsRejectedEvent() = runTest {
        repo.reject(candidate("Cup"))

        assertTrue(repo.observeDictionary().first().isEmpty())
        val events = repo.observeCatchEvents().first()
        assertEquals(1, events.size)
        assertEquals(CatchStatus.REJECTED, events.first().status)
        assertEquals(null, events.first().resolvedWordId)
        assertTrue(repo.observeReviewDue().first().isEmpty())
    }

    @Test
    fun freshRepository_isEmpty() = runTest {
        assertTrue(repo.observeDictionary().first().isEmpty())
        assertTrue(repo.observeReviewDue().first().isEmpty())
        assertTrue(repo.observeCatchEvents().first().isEmpty())
    }

    @Test
    fun confirmedWord_isImmediatelyReviewDue_andCarriesCreatedAtFromTimeProvider() = runTest {
        time.now = 2_500L
        val word = repo.confirm(candidate("Chair"), targetLanguage = "es").word

        assertEquals(2_500L, word.createdAt)
        assertEquals(null, word.translation) // keine erfundene Übersetzung
        assertEquals(listOf(word.id), repo.observeReviewDue().first().map { it.wordId })
    }

    @Test
    fun normalizationDeduplicatesLabelVariants() = runTest {
        repo.confirm(candidate("Cup"), targetLanguage = "es")
        repo.confirm(candidate("  cup "), targetLanguage = "es")

        val dictionary = repo.observeDictionary().first()
        assertEquals(1, dictionary.size)
        assertEquals("cup", dictionary.first().label)
    }

    @Test
    fun sameLabelDifferentTargetLanguage_createsTwoWords() = runTest {
        repo.confirm(candidate("Cup"), targetLanguage = "es")
        repo.confirm(candidate("Cup"), targetLanguage = "fr")

        assertEquals(2, repo.observeDictionary().first().size)
    }

    // Wiederbegegnung ist als zusätzlicher CatchEvent sichtbar, verschiebt das Scheduling aber NICHT.
    @Test
    fun reEncounter_addsCatchEvent_butDoesNotReschedule() = runTest {
        val word = repo.confirm(candidate("Plant"), targetLanguage = "es").word // createdAt = 1_000
        time.now = 9_000L
        repo.confirm(candidate("Plant"), targetLanguage = "es")

        val encounters = repo.observeCatchEvents().first().count {
            it.status == CatchStatus.CONFIRMED && it.resolvedWordId == word.id
        }
        assertEquals(2, encounters)

        // dueAt blieb bei createdAt (1_000): eine Abfrage bei now = 1_000 findet das Wort noch fällig.
        // Wäre das Scheduling fälschlich auf 9_000 gewandert, wäre es bei now = 1_000 NICHT fällig.
        time.now = 1_000L
        assertEquals(listOf(word.id), repo.observeReviewDue().first().map { it.wordId })
    }
}
