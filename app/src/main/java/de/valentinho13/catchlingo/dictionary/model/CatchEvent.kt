package de.valentinho13.catchlingo.dictionary.model

enum class CatchStatus { CONFIRMED, REJECTED }

/**
 * Append-only Begegnungs-Log. JEDE Bestätigung/Ablehnung erzeugt einen CatchEvent –
 * auch eine Wiederbegegnung desselben Worts (dann ohne neues Word).
 *
 * Wird nie überschrieben: das ist die Herkunfts- und Wiederbegegnungs-Spur, aus der später
 * Review-Scheduling abgeleitet werden KANN (in diesem Slice noch nicht genutzt).
 *
 * Bewusste Vereinfachung für Slice 1: CatchStatus deckt Bestätigt/Abgelehnt ab; ein separates
 * ConfirmationEvent bringt hier keinen Mehrwert. CorrectionEvent ist ein späterer Erweiterungspunkt.
 */
data class CatchEvent(
    val id: Long,
    val label: String,
    val confidence: Float,
    val occurredAt: Long,
    val resolvedWordId: Long?,
    val status: CatchStatus,
)
