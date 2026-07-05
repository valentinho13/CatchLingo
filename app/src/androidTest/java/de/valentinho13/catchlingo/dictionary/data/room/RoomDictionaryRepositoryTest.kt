package de.valentinho13.catchlingo.dictionary.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.valentinho13.catchlingo.core.text.LabelNormalizer
import de.valentinho13.catchlingo.core.time.FakeTimeProvider
import de.valentinho13.catchlingo.dictionary.model.CatchStatus
import de.valentinho13.catchlingo.discover.model.Candidate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomDictionaryRepositoryTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val time = FakeTimeProvider(now = 1_000L)
    private lateinit var db: CatchLingoDatabase
    private lateinit var repo: RoomDictionaryRepository

    private fun candidate(raw: String, confidence: Float = 0.9f) = Candidate(
        id = "c-$raw-${time.now}",
        rawLabel = raw,
        normalizedLabel = LabelNormalizer.normalize(raw),
        confidence = confidence,
        source = "test",
        detectedAt = time.now,
    )

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(context, CatchLingoDatabase::class.java).build()
        repo = RoomDictionaryRepository(db, time)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun confirmingSameCandidateTwice_keepsSingleWord_butRecordsTwoConfirmedEvents() = runTest {
        repo.confirm(candidate("Cup"), targetLanguage = "es")
        time.now = 5_000L
        repo.confirm(candidate("Cup"), targetLanguage = "es")

        assertEquals(1, repo.observeDictionary().first().size)
        val confirmed = repo.observeCatchEvents().first().filter { it.status == CatchStatus.CONFIRMED }
        assertEquals(2, confirmed.size)
        assertEquals(1, repo.observeReviewDue().first().size)
    }

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
    fun confirmedWord_isReviewDue() = runTest {
        time.now = 2_500L
        val word = repo.confirm(candidate("Chair"), targetLanguage = "es")

        assertEquals(2_500L, word.createdAt)
        assertEquals(null, word.translation)
        assertEquals(listOf(word.id), repo.observeReviewDue().first().map { it.wordId })
    }

    // Der eigentliche Beweis: echtes Gedächtnis über DB-Schließen/Neu-Öffnen auf derselben Datei.
    @Test
    fun reopeningDatabase_keepsWordsCatchEventsAndReviewState() = runTest {
        val dbName = "catchlingo-reopen-test.db"
        context.deleteDatabase(dbName)

        val db1 = Room.databaseBuilder(context, CatchLingoDatabase::class.java, dbName).build()
        val repo1 = RoomDictionaryRepository(db1, time)
        repo1.confirm(candidate("Cup"), targetLanguage = "es")   // 1 Word + 1 CONFIRMED + 1 ReviewState
        repo1.reject(candidate("Chair"))                          // + 1 REJECTED
        db1.close()

        val db2 = Room.databaseBuilder(context, CatchLingoDatabase::class.java, dbName).build()
        val repo2 = RoomDictionaryRepository(db2, time)
        assertEquals(1, repo2.observeDictionary().first().size)
        assertEquals(2, repo2.observeCatchEvents().first().size)
        assertEquals(1, repo2.observeReviewDue().first().size)
        db2.close()

        context.deleteDatabase(dbName)
    }
}
