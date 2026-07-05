package de.valentinho13.catchlingo.dictionary.data

import de.valentinho13.catchlingo.discover.model.Candidate
import de.valentinho13.catchlingo.dictionary.model.CatchEvent
import de.valentinho13.catchlingo.dictionary.model.ReviewState
import de.valentinho13.catchlingo.dictionary.model.Word
import kotlinx.coroutines.flow.Flow

/**
 * Eigentum des learning-data-engineer: Datenmodell + Lernfluss.
 *
 * Asynchron und beobachtbar, damit spätere Room-Persistenz und UI-Beobachtung sauber andocken:
 * Schreiboperationen sind `suspend`, Leseoperationen liefern `Flow`. ALLE
 * Duplicate-/Persistenz-/Review-Ableitungslogik lebt hinter diesem Interface – niemals im ViewModel/UI.
 * Slice A härtet nur den Kontrakt (kein Room, keine Semantik-Änderung); ein Room-Adapter erfüllt später dasselbe Interface.
 */
interface DictionaryRepository {

    /** Bestätigt einen Kandidaten. find-or-create Word (eindeutig per normalisiertem Label + Sprache)
     *  und hängt einen CONFIRMED-CatchEvent an. Wiederbegegnung → kein neues Word, aber neuer CatchEvent. */
    suspend fun confirm(candidate: Candidate, targetLanguage: String): Word

    /** Lehnt einen Kandidaten ab: kein Word, aber ein REJECTED-CatchEvent (ehrliche Diagnostik). */
    suspend fun reject(candidate: Candidate)

    /** Beobachtbarer Dictionary-Stand (dedupliziert); optional auf eine Zielsprache gefiltert. */
    fun observeDictionary(targetLanguage: String? = null): Flow<List<Word>>

    /** Beobachtbares, append-only Begegnungs-Log (für Review-Ableitung, Diagnostik und Tests). */
    fun observeCatchEvents(): Flow<List<CatchEvent>>

    /**
     * Beobachtbare Review-Fälligkeit. Bewusst OHNE `now`-Parameter: Fälligkeit ist zeitabhängig und
     * würde bei einem einmalig übergebenen `now` einfrieren. Stattdessen wird zum Emissionszeitpunkt
     * gegen die injizierte Zeitquelle ausgewertet – sauber und in Tests deterministisch (FakeTimeProvider).
     */
    fun observeReviewDue(): Flow<List<ReviewState>>
}
