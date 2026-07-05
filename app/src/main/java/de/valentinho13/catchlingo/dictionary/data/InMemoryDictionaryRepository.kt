package de.valentinho13.catchlingo.dictionary.data

import de.valentinho13.catchlingo.core.time.SystemTimeProvider
import de.valentinho13.catchlingo.core.time.TimeProvider
import de.valentinho13.catchlingo.discover.model.Candidate
import de.valentinho13.catchlingo.dictionary.model.CatchEvent
import de.valentinho13.catchlingo.dictionary.model.CatchStatus
import de.valentinho13.catchlingo.dictionary.model.ReviewState
import de.valentinho13.catchlingo.dictionary.model.Word

/**
 * In-Memory-Implementierung für Slice 1 – eine technische Vorstufe, KEIN fertiges Dictionary-Feature:
 * die Daten überleben KEINEN App-Neustart. Persistenz (Room) folgt als eigener Adapter hinter demselben
 * Interface. Nicht als gelöste Neustart-Persistenz ausgeben.
 */
class InMemoryDictionaryRepository(
    private val time: TimeProvider = SystemTimeProvider(),
) : DictionaryRepository {

    private val words = mutableListOf<Word>()
    private val events = mutableListOf<CatchEvent>()
    private val reviewStates = mutableMapOf<Long, ReviewState>()
    private var wordIdCounter = 0L
    private var eventIdCounter = 0L

    override fun confirm(candidate: Candidate, targetLanguage: String): Word {
        val now = time.nowMillis()
        val word = words.firstOrNull {
            it.label == candidate.normalizedLabel && it.targetLanguage == targetLanguage
        } ?: Word(
            id = ++wordIdCounter,
            label = candidate.normalizedLabel,
            targetLanguage = targetLanguage,
            translation = null,
            createdAt = now,
        ).also {
            words.add(it)
            // Slice-1-Regel: sofort fällig. Wiederbegegnungen verschieben dueAt bewusst nicht.
            reviewStates[it.id] = ReviewState(wordId = it.id, dueAt = it.createdAt)
        }

        events.add(
            CatchEvent(
                id = ++eventIdCounter,
                label = candidate.rawLabel,
                confidence = candidate.confidence,
                occurredAt = now,
                resolvedWordId = word.id,
                status = CatchStatus.CONFIRMED,
            )
        )
        return word
    }

    override fun reject(candidate: Candidate) {
        events.add(
            CatchEvent(
                id = ++eventIdCounter,
                label = candidate.rawLabel,
                confidence = candidate.confidence,
                occurredAt = time.nowMillis(),
                resolvedWordId = null,
                status = CatchStatus.REJECTED,
            )
        )
    }

    override fun dictionary(): List<Word> = words.toList()

    override fun reviewDue(now: Long): List<Word> =
        words.filter { word -> reviewStates[word.id]?.let { it.dueAt <= now } == true }

    override fun catchEvents(): List<CatchEvent> = events.toList()
}
