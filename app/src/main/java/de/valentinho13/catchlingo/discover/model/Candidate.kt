package de.valentinho13.catchlingo.discover.model

/**
 * Ein von der Discover-Säule vorgeschlagener Objektkandidat – transient, noch NICHT persistiert.
 * Persistenz beginnt erst bei Bestätigung (siehe DictionaryRepository).
 *
 * `normalizedLabel` wird in der Domain gesetzt (LabelNormalizer), nie in der UI.
 * In diesem Slice stammt der Kandidat aus einer Mock-Quelle; echte CameraX/ML-Kit-Erkennung folgt später.
 */
data class Candidate(
    val id: String,
    val rawLabel: String,
    val normalizedLabel: String,
    val confidence: Float,
    val source: String,
    val detectedAt: Long,
)
