package de.valentinho13.catchlingo.dictionary.data

import de.valentinho13.catchlingo.core.time.SystemTimeProvider
import de.valentinho13.catchlingo.core.time.TimeProvider
import de.valentinho13.catchlingo.discover.model.Candidate
import de.valentinho13.catchlingo.dictionary.model.CatchEvent
import de.valentinho13.catchlingo.dictionary.model.CatchStatus
import de.valentinho13.catchlingo.dictionary.model.ReviewState
import de.valentinho13.catchlingo.dictionary.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * In-Memory-Implementierung – eine technische Vorstufe, KEIN fertiges Dictionary-Feature:
 * die Daten überleben KEINEN App-Neustart. Persistenz (Room) folgt als eigener Adapter hinter demselben
 * Interface. Nicht als gelöste Neustart-Persistenz ausgeben.
 *
 * Beobachtbarkeit über einen einzelnen [MutableStateFlow] eines unveränderlichen Snapshots (eine
 * Emission pro Mutation). Mutationen sind per [Mutex] serialisiert – kein GlobalScope, keine Delays,
 * keine Main-Thread-DB-Simulation.
 */
class InMemoryDictionaryRepository(
    private val time: TimeProvider = SystemTimeProvider(),
) : DictionaryRepository {

    private data class Snapshot(
        val words: List<Word> = emptyList(),
        val events: List<CatchEvent> = emptyList(),
        val reviewStates: Map<Long, ReviewState> = emptyMap(),
    )

    private val state = MutableStateFlow(Snapshot())
    private val mutex = Mutex()
    private var wordIdCounter = 0L
    private var eventIdCounter = 0L

    override suspend fun confirm(candidate: Candidate, targetLanguage: String): ConfirmResult = mutex.withLock {
        val now = time.nowMillis()
        val current = state.value
        val existing = current.words.firstOrNull {
            it.label == candidate.normalizedLabel && it.targetLanguage == targetLanguage
        }
        val word = existing ?: Word(
            id = ++wordIdCounter,
            label = candidate.normalizedLabel,
            targetLanguage = targetLanguage,
            translation = null,
            createdAt = now,
        )

        val event = CatchEvent(
            id = ++eventIdCounter,
            label = candidate.rawLabel,
            confidence = candidate.confidence,
            occurredAt = now,
            resolvedWordId = word.id,
            status = CatchStatus.CONFIRMED,
        )

        state.value = if (existing == null) {
            current.copy(
                words = current.words + word,
                events = current.events + event,
                // Slice-1-Regel: sofort fällig. Wiederbegegnungen verschieben dueAt bewusst nicht.
                reviewStates = current.reviewStates + (word.id to ReviewState(word.id, dueAt = word.createdAt)),
            )
        } else {
            current.copy(events = current.events + event)
        }
        ConfirmResult(word = word, isNew = existing == null)
    }

    override suspend fun reject(candidate: Candidate) = mutex.withLock {
        val event = CatchEvent(
            id = ++eventIdCounter,
            label = candidate.rawLabel,
            confidence = candidate.confidence,
            occurredAt = time.nowMillis(),
            resolvedWordId = null,
            status = CatchStatus.REJECTED,
        )
        state.value = state.value.let { it.copy(events = it.events + event) }
    }

    override fun observeDictionary(targetLanguage: String?): Flow<List<Word>> =
        state.map { snap ->
            snap.words.filter { targetLanguage == null || it.targetLanguage == targetLanguage }
        }

    override fun observeCatchEvents(): Flow<List<CatchEvent>> =
        state.map { it.events }

    override fun observeReviewDue(): Flow<List<ReviewState>> =
        state.map { snap ->
            val now = time.nowMillis()
            snap.reviewStates.values.filter { it.dueAt <= now }
        }
}
