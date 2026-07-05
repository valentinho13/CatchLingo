package de.valentinho13.catchlingo.discover

import de.valentinho13.catchlingo.core.text.LabelNormalizer
import de.valentinho13.catchlingo.core.time.SystemTimeProvider
import de.valentinho13.catchlingo.core.time.TimeProvider
import de.valentinho13.catchlingo.discover.model.Candidate

/**
 * DEV-FIXTURE. Erzeugt Mock-Kandidaten für Slice 1 – dies ist KEINE echte Objekterkennung und darf
 * niemals als solche ausgegeben werden. In einem späteren Slice ersetzt eine CameraX + ML-Kit-Quelle
 * diese Klasse hinter derselben Idee (Kandidaten liefern, nichts persistieren).
 */
class MockCandidateSource(
    private val time: TimeProvider = SystemTimeProvider(),
) {
    fun candidateFor(rawLabel: String, confidence: Float = 0.9f): Candidate {
        val now = time.nowMillis()
        return Candidate(
            id = "mock-$now-${rawLabel.hashCode()}",
            rawLabel = rawLabel,
            normalizedLabel = LabelNormalizer.normalize(rawLabel),
            confidence = confidence,
            source = "mock",
            detectedAt = now,
        )
    }

    /** Ein paar Beispiel-Kandidaten für manuelle Durchläufe – rein zur Entwicklung. */
    fun sampleBatch(): List<Candidate> =
        listOf(candidateFor("Cup"), candidateFor("Chair"), candidateFor("Plant"))
}
