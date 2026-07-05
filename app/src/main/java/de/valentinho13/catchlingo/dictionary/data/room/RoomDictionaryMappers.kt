package de.valentinho13.catchlingo.dictionary.data.room

import de.valentinho13.catchlingo.dictionary.data.room.entity.CatchEventEntity
import de.valentinho13.catchlingo.dictionary.data.room.entity.ReviewStateEntity
import de.valentinho13.catchlingo.dictionary.data.room.entity.WordEntity
import de.valentinho13.catchlingo.dictionary.model.CatchEvent
import de.valentinho13.catchlingo.dictionary.model.CatchStatus
import de.valentinho13.catchlingo.dictionary.model.ReviewState
import de.valentinho13.catchlingo.dictionary.model.Word

/** Entity ↔ Domain. Domain-Modelle bleiben rein (kein Room-Import dort). */

fun WordEntity.toDomain(): Word =
    Word(id = id, label = label, targetLanguage = targetLanguage, translation = translation, createdAt = createdAt)

fun CatchEventEntity.toDomain(): CatchEvent =
    CatchEvent(
        id = id,
        label = label,
        confidence = confidence,
        occurredAt = occurredAt,
        resolvedWordId = resolvedWordId,
        status = CatchStatus.valueOf(status),
    )

fun ReviewStateEntity.toDomain(): ReviewState =
    ReviewState(wordId = wordId, dueAt = dueAt, lastReviewedAt = lastReviewedAt)
