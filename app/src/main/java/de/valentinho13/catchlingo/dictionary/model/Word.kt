package de.valentinho13.catchlingo.dictionary.model

/**
 * Der eindeutige Lerneintrag im Dictionary.
 *
 * Eindeutig per (label, targetLanguage): dasselbe Objekt in derselben Zielsprache ist genau EIN Word.
 * `label` ist die normalisierte, kanonische Form. `translation` bleibt bewusst null –
 * es werden in diesem Slice KEINE Übersetzungen erfunden.
 */
data class Word(
    val id: Long,
    val label: String,
    val targetLanguage: String,
    val translation: String? = null,
    val createdAt: Long,
)
