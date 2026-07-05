package de.valentinho13.catchlingo.dictionary.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.valentinho13.catchlingo.dictionary.data.room.entity.CatchEventEntity
import de.valentinho13.catchlingo.dictionary.data.room.entity.ReviewStateEntity
import de.valentinho13.catchlingo.dictionary.data.room.entity.WordEntity
import kotlinx.coroutines.flow.Flow

/**
 * Schlanke CRUD-DAO. Die find-or-create-Transaktion von `confirm` orchestriert das Repository
 * (db.withTransaction), damit die DAO frei von Ablauflogik bleibt.
 *
 * Bewusst KEIN `:now` in observeReviewStates – die Zeitfilterung passiert im Repository gegen die
 * injizierte TimeProvider (kein eingefrorenes now, gleiche Semantik wie InMemory).
 */
@Dao
interface DictionaryDao {

    @Query("SELECT * FROM words WHERE label = :label AND targetLanguage = :targetLanguage LIMIT 1")
    suspend fun findWord(label: String, targetLanguage: String): WordEntity?

    @Insert
    suspend fun insertWord(word: WordEntity): Long

    @Insert
    suspend fun insertReviewState(state: ReviewStateEntity)

    @Insert
    suspend fun insertCatchEvent(event: CatchEventEntity): Long

    @Query("SELECT * FROM words")
    fun observeWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE targetLanguage = :targetLanguage")
    fun observeWordsByLanguage(targetLanguage: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM catch_events")
    fun observeCatchEvents(): Flow<List<CatchEventEntity>>

    @Query("SELECT * FROM review_states")
    fun observeReviewStates(): Flow<List<ReviewStateEntity>>
}
