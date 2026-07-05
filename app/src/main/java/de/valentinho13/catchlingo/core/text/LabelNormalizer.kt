package de.valentinho13.catchlingo.core.text

/**
 * Label-Normalisierung lebt in der Domain, NICHT in der UI.
 * Kanonische Form eines erkannten Begriffs für Dedup/Vergleich.
 */
object LabelNormalizer {
    fun normalize(raw: String): String =
        raw.trim().lowercase().replace(Regex("\\s+"), " ")
}
