package de.valentinho13.catchlingo.dictionary.data

import de.valentinho13.catchlingo.discover.model.Candidate
import de.valentinho13.catchlingo.dictionary.model.CatchEvent
import de.valentinho13.catchlingo.dictionary.model.Word

/**
 * Eigentum des learning-data-engineer: Datenmodell + Lernfluss.
 *
 * Diese Grenze schützt den Kern: ALLE Duplicate-/Persistenz-/Review-Ableitungslogik lebt hinter
 * diesem Interface – niemals im ViewModel oder in der UI. Slice 1 liefert eine In-Memory-Implementierung;
 * ein Room-Adapter kann später dasselbe Interface erfüllen, ohne UI/Tests zu ändern.
 */
interface DictionaryRepository {

    /** Bestätigt einen Kandidaten. find-or-create Word (eindeutig per normalisiertem Label + Sprache)
     *  und hängt einen CONFIRMED-CatchEvent an. Wiederbegegnung → kein neues Word, aber neuer CatchEvent. */
    fun confirm(candidate: Candidate, targetLanguage: String): Word

    /** Lehnt einen Kandidaten ab: kein Word, aber ein REJECTED-CatchEvent (ehrliche Diagnostik). */
    fun reject(candidate: Candidate)

    /** Aktueller Dictionary-Stand (dedupliziert). */
    fun dictionary(): List<Word>

    /** Alle Wörter, deren ReviewState zum Zeitpunkt `now` fällig ist. */
    fun reviewDue(now: Long): List<Word>

    /** Append-only Begegnungs-Log (für Review-Ableitung, Diagnostik und Tests). */
    fun catchEvents(): List<CatchEvent>
}
