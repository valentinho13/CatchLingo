package de.valentinho13.catchlingo.dictionary.model

/**
 * Erklärbarer Review-Zustand pro Word.
 *
 * Slice-1-Regel: `dueAt = Word.createdAt` → ein bestätigtes Wort ist sofort review-fällig.
 * Wiederbegegnungen ändern den Zeitplan hier bewusst NICHT (Scheduling reagiert noch nicht auf
 * CatchEvents), verhindern es aber auch nicht: die Daten dafür liegen im CatchEvent-Log bereit.
 */
data class ReviewState(
    val wordId: Long,
    val dueAt: Long,
    val lastReviewedAt: Long? = null,
)
