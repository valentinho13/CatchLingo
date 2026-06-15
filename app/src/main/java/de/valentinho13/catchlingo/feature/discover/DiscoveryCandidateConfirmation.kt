package de.valentinho13.catchlingo.feature.discover

internal data class MlLabelObservation(
    val text: String,
    val confidence: Float,
)

internal data class DiscoveryCandidate(
    val labelText: String,
    val confidence: Float,
    val match: VocabularyMatch,
    val score: Float = confidence,
    val matchedLabel: String = labelText,
    val supportingLabels: List<String> = listOf(labelText),
    val context: DetectionContext = DetectionContext.Generic,
    val contextBoost: Float = 0f,
    val riskPenalty: Float = 0f,
    val requiresConfirmation: Boolean = false,
)

internal enum class ConfirmationReason {
    RiskyWord,
    NearThreshold,
    AmbiguousLabels,
    SoftCandidate,
}

internal sealed interface CandidateConfirmationOption {
    data class Candidate(val match: VocabularyMatch) : CandidateConfirmationOption
    data object NoneOfThese : CandidateConfirmationOption
}

internal data class PendingDiscoveryConfirmation(
    val originalLabels: List<MlLabelObservation>,
    val candidates: List<DiscoveryCandidate>,
    val proposedWord: VocabularyMatch,
    val reasons: Set<ConfirmationReason>,
    val options: List<CandidateConfirmationOption>,
) {
    val requiresConfirmation: Boolean = true
}

internal fun buildPendingConfirmation(
    originalLabels: List<MlLabelObservation>,
    candidates: List<DiscoveryCandidate>,
    proposedWord: VocabularyMatch,
    acceptedConfidence: Float,
): PendingDiscoveryConfirmation? {
    val reasons = buildSet {
        val proposedCandidate = candidates.firstOrNull { it.match.id == proposedWord.id }
        if (proposedWord.id in RiskyConfirmationWordIds) {
            add(ConfirmationReason.RiskyWord)
        }
        if (proposedCandidate?.requiresConfirmation == true) {
            add(ConfirmationReason.SoftCandidate)
        }
        if (acceptedConfidence - proposedWord.minConfidence <= NearThresholdMargin) {
            add(ConfirmationReason.NearThreshold)
        }
        if (candidates.hasAmbiguousCompetingLabels(proposedWord)) {
            add(ConfirmationReason.AmbiguousLabels)
        }
    }
    if (reasons.isEmpty()) return null

    val options = candidates
        .asSequence()
        .distinctBy { it.match.id }
        .take(MaxConfirmationCandidates)
        .map { CandidateConfirmationOption.Candidate(it.match) }
        .toList() + CandidateConfirmationOption.NoneOfThese

    return PendingDiscoveryConfirmation(
        originalLabels = originalLabels.take(MaxLoggedLabels),
        candidates = candidates
            .distinctBy { it.match.id }
            .take(MaxConfirmationCandidates),
        proposedWord = proposedWord,
        reasons = reasons,
        options = options,
    )
}

internal fun buildDebugPendingConfirmation(
    originalLabels: List<MlLabelObservation>,
    candidates: List<DiscoveryCandidate>,
): PendingDiscoveryConfirmation? {
    val distinctCandidates = candidates
        .distinctBy { it.match.id }
        .take(MaxConfirmationCandidates)
    val proposedWord = distinctCandidates.firstOrNull()?.match ?: return null
    return PendingDiscoveryConfirmation(
        originalLabels = originalLabels.take(MaxLoggedLabels),
        candidates = distinctCandidates,
        proposedWord = proposedWord,
        reasons = setOf(ConfirmationReason.SoftCandidate),
        options = distinctCandidates.map { CandidateConfirmationOption.Candidate(it.match) } +
            CandidateConfirmationOption.NoneOfThese,
    )
}

private fun List<DiscoveryCandidate>.hasAmbiguousCompetingLabels(proposedWord: VocabularyMatch): Boolean {
    val proposed = firstOrNull { it.match.id == proposedWord.id } ?: return false
    return any { candidate ->
        candidate.match.id != proposedWord.id &&
            proposed.confidence - candidate.confidence <= AmbiguousConfidenceMargin
    }
}

internal const val MaxCorrectionHistoryEvents = 100

internal data class DiscoveryCorrectionEvent(
    val timestampMillis: Long,
    val labels: List<MlLabelObservation>,
    val shownCandidateIds: List<String>,
    val selectedCandidateId: String?,
)

internal class DiscoveryCorrectionHistory(
    private val events: List<DiscoveryCorrectionEvent> = emptyList(),
) {
    fun add(event: DiscoveryCorrectionEvent): DiscoveryCorrectionHistory =
        DiscoveryCorrectionHistory((events + event).takeLast(MaxCorrectionHistoryEvents))

    fun entries(): List<DiscoveryCorrectionEvent> = events
}

private val RiskyConfirmationWordIds = setOf("anjing", "gelas", "kursi", "meja", "ponsel", "wastafel")
private const val NearThresholdMargin = 0.04f
private const val AmbiguousConfidenceMargin = 0.08f
private const val MaxConfirmationCandidates = 4
private const val MaxLoggedLabels = 5
