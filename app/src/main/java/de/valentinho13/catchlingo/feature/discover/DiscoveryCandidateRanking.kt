package de.valentinho13.catchlingo.feature.discover

import kotlin.math.max

internal enum class DetectionContext {
    HomeBedroom,
    Kitchen,
    Generic,
}

internal data class RankedDiscoveryCandidates(
    val context: DetectionContext,
    val candidates: List<DiscoveryCandidate>,
)

internal fun rankDiscoveryCandidates(
    labels: List<MlLabelObservation>,
    directCandidates: List<DiscoveryCandidate>,
): RankedDiscoveryCandidates {
    val evidence = labels.map { label ->
        LabelEvidence(
            text = label.text,
            normalized = label.text.lowercase(),
            confidence = label.confidence,
        )
    }
    val context = inferDetectionContext(evidence)
    val rankedDirect = directCandidates.map { candidate ->
        candidate.withRanking(
            context = context,
            supportingLabels = evidence.matching(candidate.labelText),
            allLabels = evidence,
        )
    }
    val evidenceCandidates = buildEvidenceCandidates(evidence, context)
    val ranked = (rankedDirect + evidenceCandidates)
        .groupBy { it.match.id }
        .map { (_, candidates) -> candidates.maxBy { it.score } }
        .sortedWith(compareByDescending<DiscoveryCandidate> { it.score }.thenBy { it.match.word })

    return RankedDiscoveryCandidates(context = context, candidates = ranked)
}

internal fun DiscoveryCandidate.canAutoCatch(): Boolean =
    confidence >= match.minConfidence && !requiresConfirmation

private fun DiscoveryCandidate.withRanking(
    context: DetectionContext,
    supportingLabels: List<LabelEvidence>,
    allLabels: List<LabelEvidence> = supportingLabels,
): DiscoveryCandidate {
    val labels = supportingLabels.ifEmpty {
        listOf(LabelEvidence(labelText, labelText.lowercase(), confidence))
    }
    val riskPenalty = riskPenaltyFor(match.id, allLabels)
    val contextBoost = contextBoostFor(match.id, context)
    val requiresConfirmation = requiresConfirmationFor(
        id = match.id,
        context = context,
        labels = allLabels,
        riskPenalty = riskPenalty,
    )
    val supportBoost = (labels.size - 1).coerceAtLeast(0) * SupportBoostPerLabel
    val score = (confidence + contextBoost + supportBoost - riskPenalty).coerceIn(0f, 1f)
    val bestLabel = labels.maxByOrNull { it.confidence } ?: labels.first()

    return copy(
        score = score,
        matchedLabel = bestLabel.text,
        supportingLabels = labels.map { it.text }.distinct(),
        context = context,
        contextBoost = contextBoost,
        riskPenalty = riskPenalty,
        requiresConfirmation = requiresConfirmation,
    )
}

private fun buildEvidenceCandidates(
    evidence: List<LabelEvidence>,
    context: DetectionContext,
): List<DiscoveryCandidate> = buildList {
    addEvidenceCandidate(
        id = "mangkuk",
        evidence = evidence,
        context = context,
        directTerms = setOf("bowl", "soup bowl"),
        supportingTerms = setOf("container", "dish", "tableware"),
    )
    addEvidenceCandidate(
        id = "piring",
        evidence = evidence,
        context = context,
        directTerms = setOf("plate"),
        supportingTerms = setOf("dish"),
    )
    addEvidenceCandidate(
        id = "cangkir",
        evidence = evidence,
        context = context,
        directTerms = setOf("cup", "mug"),
        supportingTerms = setOf("coffee cup", "coffee mug"),
    )
    addEvidenceCandidate(
        id = "botol",
        evidence = evidence,
        context = context,
        directTerms = setOf("bottle", "water bottle", "plastic bottle"),
    )
    addEvidenceCandidate(
        id = "wastafel",
        evidence = evidence,
        context = context,
        directTerms = setOf("kitchen sink"),
        supportingTerms = setOf("sink", "faucet", "tap"),
        forceConfirmation = true,
    )
    addEvidenceCandidate(
        id = "bunga",
        evidence = evidence,
        context = context,
        directTerms = setOf("flower", "blossom"),
        supportingTerms = setOf("plant"),
    )
}

private fun MutableList<DiscoveryCandidate>.addEvidenceCandidate(
    id: String,
    evidence: List<LabelEvidence>,
    context: DetectionContext,
    directTerms: Set<String>,
    supportingTerms: Set<String> = emptySet(),
    forceConfirmation: Boolean = true,
) {
    val direct = evidence.matchingAny(directTerms)
    val supporting = evidence.matchingAny(supportingTerms)
    val matchedEvidence = direct + supporting
    if (matchedEvidence.isEmpty()) return
    val match = vocabularyById(id) ?: return
    val best = matchedEvidence.maxBy { it.confidence }
    val base = DiscoveryCandidate(
        labelText = best.text,
        confidence = best.confidence,
        match = match,
    ).withRanking(context = context, supportingLabels = matchedEvidence, allLabels = evidence)
    add(
        base.copy(
            requiresConfirmation = forceConfirmation || base.requiresConfirmation,
            riskPenalty = max(base.riskPenalty, if (forceConfirmation) ConfirmationFirstPenalty else base.riskPenalty),
            score = (
                base.confidence +
                    base.contextBoost +
                    ((matchedEvidence.size - 1).coerceAtLeast(0) * SupportBoostPerLabel) -
                    max(base.riskPenalty, if (forceConfirmation) ConfirmationFirstPenalty else base.riskPenalty)
                ).coerceIn(0f, 1f),
        ),
    )
}

private fun inferDetectionContext(evidence: List<LabelEvidence>): DetectionContext {
    val kitchenScore = evidence.countMatches(KitchenContextTerms)
    val homeScore = evidence.countMatches(HomeBedroomContextTerms)
    return when {
        kitchenScore > 0 && kitchenScore >= homeScore -> DetectionContext.Kitchen
        homeScore > 0 -> DetectionContext.HomeBedroom
        else -> DetectionContext.Generic
    }
}

private fun contextBoostFor(id: String, context: DetectionContext): Float = when {
    context == DetectionContext.Kitchen && id in KitchenPriorityIds -> KitchenContextBoost
    context == DetectionContext.HomeBedroom && id in HomeBedroomPriorityIds -> HomeBedroomContextBoost
    else -> 0f
}

private fun riskPenaltyFor(id: String, labels: List<LabelEvidence>): Float = when {
    id == "wastafel" -> SinkRiskPenalty
    id == "kursi" && labels.anyMatches(ChairAmbiguityTerms) -> StrongRiskPenalty
    id == "ponsel" && labels.anyMatches(PhoneAmbiguityTerms) -> StrongRiskPenalty
    id == "meja" && labels.anyMatches(TableAmbiguityTerms) -> StrongRiskPenalty
    id in ConfirmationFirstIds -> ConfirmationFirstPenalty
    else -> 0f
}

private fun requiresConfirmationFor(
    id: String,
    context: DetectionContext,
    labels: List<LabelEvidence>,
    riskPenalty: Float,
): Boolean = id in ConfirmationFirstIds ||
    (context == DetectionContext.Kitchen && id in KitchenPriorityIds) ||
    (context == DetectionContext.HomeBedroom && id in HomeBedroomPriorityIds) ||
    riskPenalty >= ConfirmationFirstPenalty ||
    labels.anyMatches(BroadAmbiguityTerms)

private fun List<LabelEvidence>.matching(labelText: String): List<LabelEvidence> {
    val normalized = labelText.lowercase()
    return filter { it.normalized == normalized || it.normalized.contains(normalized) }
}

private fun List<LabelEvidence>.matchingAny(terms: Set<String>): List<LabelEvidence> =
    filter { evidence -> terms.any { term -> evidence.normalized == term || evidence.normalized.contains(term) } }

private fun List<LabelEvidence>.anyMatches(terms: Set<String>): Boolean =
    any { evidence -> terms.any { term -> evidence.normalized == term || evidence.normalized.contains(term) } }

private fun List<LabelEvidence>.countMatches(terms: Set<String>): Int =
    count { evidence -> terms.any { term -> evidence.normalized == term || evidence.normalized.contains(term) } }

private data class LabelEvidence(
    val text: String,
    val normalized: String,
    val confidence: Float,
)

private val KitchenPriorityIds = setOf(
    "piring",
    "mangkuk",
    "cangkir",
    "gelas",
    "botol",
    "sendok",
    "garpu",
    "pisau",
    "panci",
    "wajan",
    "ketel",
    "teko",
    "kulkas",
    "microwave",
    "oven",
    "toaster",
    "wastafel",
    "keran",
    "talenan",
    "lemari",
)
private val HomeBedroomPriorityIds = setOf(
    "kursi",
    "meja",
    "sofa",
    "tempat_tidur",
    "buku",
    "laptop",
    "televisi",
    "lampu",
    "jam",
    "pintu",
    "jendela",
)
private val ConfirmationFirstIds = setOf("anjing", "gelas", "kursi", "meja", "ponsel", "wastafel")
private val KitchenContextTerms = setOf(
    "kitchen",
    "bowl",
    "container",
    "dish",
    "tableware",
    "plate",
    "cup",
    "mug",
    "bottle",
    "sink",
    "faucet",
    "refrigerator",
    "microwave",
    "oven",
    "toaster",
    "kettle",
    "teapot",
    "pan",
    "pot",
    "spoon",
    "fork",
    "knife",
    "cutting board",
    "food",
    "meal",
)
private val HomeBedroomContextTerms = setOf(
    "bed",
    "bedroom",
    "pillow",
    "blanket",
    "chair",
    "sofa",
    "couch",
    "television",
    "tv",
    "lamp",
    "cabinet",
    "furniture",
    "desk",
    "book",
    "laptop",
)
private val ChairAmbiguityTerms = setOf("metal", "musical instrument", "product", "vehicle", "television", "stand", "tripod")
private val PhoneAmbiguityTerms = setOf("remote", "remote control", "product", "electronics", "electronic device")
private val TableAmbiguityTerms = setOf("vegetable", "tableware", "tablecloth", "product")
private val BroadAmbiguityTerms = setOf("product", "object", "kitchen", "food", "meal", "tableware")
private const val KitchenContextBoost = 0.08f
private const val HomeBedroomContextBoost = 0.04f
private const val SupportBoostPerLabel = 0.04f
private const val ConfirmationFirstPenalty = 0.05f
private const val SinkRiskPenalty = 0.18f
private const val StrongRiskPenalty = 0.24f
