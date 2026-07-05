package de.valentinho13.catchlingo.feature.dictionary

import de.valentinho13.catchlingo.core.time.FakeTimeProvider
import de.valentinho13.catchlingo.dictionary.data.ConfirmResult
import de.valentinho13.catchlingo.dictionary.data.DictionaryRepository
import de.valentinho13.catchlingo.dictionary.data.InMemoryDictionaryRepository
import de.valentinho13.catchlingo.dictionary.model.CatchEvent
import de.valentinho13.catchlingo.dictionary.model.ReviewState
import de.valentinho13.catchlingo.dictionary.model.Word
import de.valentinho13.catchlingo.discover.model.Candidate
import de.valentinho13.catchlingo.feature.discover.VocabularyMatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DictionaryViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun confirm_addsWordToDisplayWords() = runTest(dispatcher) {
        val time = FakeTimeProvider(now = 2_500L)
        val viewModel = DictionaryViewModel(InMemoryDictionaryRepository(time), time)

        assertTrue(viewModel.confirm(cupMatch))
        advanceUntilIdle()

        assertEquals(listOf("cangkir"), viewModel.words.value.map { it.id })
        assertEquals(2_500L, viewModel.words.value.single().discoveredAtMillis)
    }

    @Test
    fun confirmingSameVocabularyIdTwice_keepsSingleDisplayWord() = runTest(dispatcher) {
        val time = FakeTimeProvider(now = 1_000L)
        val viewModel = DictionaryViewModel(InMemoryDictionaryRepository(time), time)

        assertTrue(viewModel.confirm(cupMatch))
        advanceUntilIdle()
        time.now = 5_000L
        assertFalse(viewModel.confirm(cupMatch))
        advanceUntilIdle()

        assertEquals(1, viewModel.words.value.size)
        assertEquals("cangkir", viewModel.words.value.single().id)
    }

    @Test
    fun immediateConfirmAfterColdStart_usesRepositoryDuplicateTruth() = runTest(dispatcher) {
        val time = FakeTimeProvider(now = 1_000L)
        val repository = InMemoryDictionaryRepository(time)
        repository.confirm(candidate(cupMatch, time.now), targetLanguage = "id")

        val recreatedViewModel = DictionaryViewModel(repository, time)

        assertFalse(recreatedViewModel.confirm(cupMatch))
        advanceUntilIdle()
        assertEquals(1, recreatedViewModel.words.value.size)
    }

    @Test
    fun repositoryFailure_isPropagatedInsteadOfReportedAsSuccess() = runTest(dispatcher) {
        val viewModel = DictionaryViewModel(ThrowingDictionaryRepository, FakeTimeProvider())

        try {
            viewModel.confirm(cupMatch)
            fail("confirm must not report success when persistence fails")
        } catch (error: IllegalStateException) {
            assertEquals("write failed", error.message)
        }
        advanceUntilIdle()
        assertTrue(viewModel.words.value.isEmpty())
    }

    private fun candidate(match: VocabularyMatch, now: Long) = Candidate(
        id = "${match.id}-$now",
        rawLabel = match.source,
        normalizedLabel = match.id,
        confidence = 0.9f,
        source = "test",
        detectedAt = now,
    )

    private val cupMatch = VocabularyMatch(
        id = "cangkir",
        word = "cangkir",
        source = "cup",
        category = "Essen & Trinken",
    )

    private object ThrowingDictionaryRepository : DictionaryRepository {
        override suspend fun confirm(candidate: Candidate, targetLanguage: String): ConfirmResult =
            throw IllegalStateException("write failed")

        override suspend fun reject(candidate: Candidate) = Unit

        override fun observeDictionary(targetLanguage: String?): Flow<List<Word>> = flowOf(emptyList())

        override fun observeCatchEvents(): Flow<List<CatchEvent>> = flowOf(emptyList())

        override fun observeReviewDue(): Flow<List<ReviewState>> = flowOf(emptyList())
    }
}
