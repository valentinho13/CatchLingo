package de.valentinho13.catchlingo.dictionary.data.room

import androidx.room.withTransaction
import de.valentinho13.catchlingo.core.time.TimeProvider
import de.valentinho13.catchlingo.dictionary.data.DictionaryRepository
import de.valentinho13.catchlingo.dictionary.data.ConfirmResult
import de.valentinho13.catchlingo.dictionary.data.room.entity.CatchEventEntity
import de.valentinho13.catchlingo.dictionary.data.room.entity.ReviewStateEntity
import de.valentinho13.catchlingo.dictionary.data.room.entity.WordEntity
import de.valentinho13.catchlingo.dictionary.model.CatchEvent
import de.valentinho13.catchlingo.dictionary.model.CatchStatus
import de.valentinho13.catchlingo.dictionary.model.ReviewState
import de.valentinho13.catchlingo.dictionary.model.Word
import de.valentinho13.catchlingo.discover.model.Candidate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-basierte Implementierung des [DictionaryRepository]-Kontrakts – erfüllt dieselbe Semantik wie
 * die In-Memory-Vorstufe, aber persistent. `confirm` läuft transaktional (find-or-create + Events in
 * einer Transaktion), damit Observer genau eine konsistente Emission sehen und der Unique-Index nie
 * zum Insert-Konflikt führt. Zeit weiterhin ausschließlich über [TimeProvider].
 */
class RoomDictionaryRepository(
    private val db: CatchLingoDatabase,
    private val time: TimeProvider,
) : DictionaryRepository {

    private val dao = db.dictionaryDao()

    override suspend fun confirm(candidate: Candidate, targetLanguage: String): ConfirmResult =
        db.withTransaction {
            val now = time.nowMillis()
            val existing = dao.findWord(candidate.normalizedLabel, targetLanguage)
            val word: Word = if (existing == null) {
                val newId = dao.insertWord(
                    WordEntity(
                        label = candidate.normalizedLabel,
                        targetLanguage = targetLanguage,
                        translation = null,
                        createdAt = now,
                    ),
                )
                // Slice-Regel: sofort fällig. Wiederbegegnungen verschieben dueAt bewusst nicht.
                dao.insertReviewState(ReviewStateEntity(wordId = newId, dueAt = now, lastReviewedAt = null))
                Word(id = newId, label = candidate.normalizedLabel, targetLanguage = targetLanguage, translation = null, createdAt = now)
            } else {
                existing.toDomain()
            }

            dao.insertCatchEvent(
                CatchEventEntity(
                    label = candidate.rawLabel,
                    confidence = candidate.confidence,
                    occurredAt = now,
                    resolvedWordId = word.id,
                    status = CatchStatus.CONFIRMED.name,
                ),
            )
            ConfirmResult(word = word, isNew = existing == null)
        }

    override suspend fun reject(candidate: Candidate) {
        dao.insertCatchEvent(
            CatchEventEntity(
                label = candidate.rawLabel,
                confidence = candidate.confidence,
                occurredAt = time.nowMillis(),
                resolvedWordId = null,
                status = CatchStatus.REJECTED.name,
            ),
        )
    }

    override fun observeDictionary(targetLanguage: String?): Flow<List<Word>> =
        (if (targetLanguage == null) dao.observeWords() else dao.observeWordsByLanguage(targetLanguage))
            .map { entities -> entities.map(WordEntity::toDomain) }

    override fun observeCatchEvents(): Flow<List<CatchEvent>> =
        dao.observeCatchEvents().map { entities -> entities.map(CatchEventEntity::toDomain) }

    override fun observeReviewDue(): Flow<List<ReviewState>> =
        dao.observeReviewStates().map { entities ->
            val now = time.nowMillis()
            entities.map(ReviewStateEntity::toDomain).filter { it.dueAt <= now }
        }
}
