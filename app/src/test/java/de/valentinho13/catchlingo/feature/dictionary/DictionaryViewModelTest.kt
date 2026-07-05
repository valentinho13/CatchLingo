package de.valentinho13.catchlingo.feature.dictionary

import de.valentinho13.catchlingo.core.time.FakeTimeProvider
import de.valentinho13.catchlingo.dictionary.data.InMemoryDictionaryRepository
import de.valentinho13.catchlingo.feature.discover.VocabularyMatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        viewModel.confirm(cupMatch)
        advanceUntilIdle()

        assertEquals(1, viewModel.words.value.size)
        assertEquals("cangkir", viewModel.words.value.single().id)
    }

    private val cupMatch = VocabularyMatch(
        id = "cangkir",
        word = "cangkir",
        source = "cup",
        category = "Essen & Trinken",
    )
}
