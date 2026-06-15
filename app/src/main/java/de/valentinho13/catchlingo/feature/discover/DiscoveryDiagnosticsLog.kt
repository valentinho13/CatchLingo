package de.valentinho13.catchlingo.feature.discover

import android.content.Context
import android.util.Log
import java.util.Locale

internal enum class DiscoveryDiagnosticDecision {
    AutoAccepted,
    PendingConfirmation,
    UserConfirmed,
    UserRejectedNoneOfThese,
    AlreadyKnown,
    Ignored,
    DuplicateBlocked,
}

internal data class DiscoveryDiagnosticCandidate(
    val id: String,
    val word: String,
    val source: String,
    val category: String,
    val labelText: String,
    val confidence: Float,
    val score: Float = confidence,
    val matchedLabel: String = labelText,
    val supportingLabels: List<String> = listOf(labelText),
    val context: String = DetectionContext.Generic.name,
    val contextBoost: Float = 0f,
    val riskPenalty: Float = 0f,
    val requiresConfirmation: Boolean = false,
)

internal data class DiscoveryDiagnosticEvent(
    val timestampMillis: Long,
    val labels: List<MlLabelObservation>,
    val candidates: List<DiscoveryDiagnosticCandidate>,
    val proposedCandidateId: String?,
    val finalCandidateId: String?,
    val selectedCandidateId: String?,
    val decision: DiscoveryDiagnosticDecision,
    val reasons: List<String> = emptyList(),
    val objectDetection: ObjectDetectionDiagnostics? = null,
) {
    fun toJson(): String = buildString {
        append("{")
        appendJsonField("timestampMillis", timestampMillis)
        append(",")
        appendJsonField("decision", decision.name)
        append(",")
        appendJsonField("proposedCandidateId", proposedCandidateId)
        append(",")
        appendJsonField("finalCandidateId", finalCandidateId)
        append(",")
        appendJsonField("selectedCandidateId", selectedCandidateId)
        append(",\"labels\":")
        append(labels.toJsonArray { label ->
            buildString {
                append("{")
                appendJsonField("text", label.text)
                append(",")
                appendJsonField("confidence", label.confidence)
                append("}")
            }
        })
        append(",\"candidates\":")
        append(candidates.toJsonArray { candidate ->
            buildString {
                append("{")
                appendJsonField("id", candidate.id)
                append(",")
                appendJsonField("word", candidate.word)
                append(",")
                appendJsonField("source", candidate.source)
                append(",")
                appendJsonField("category", candidate.category)
                append(",")
                appendJsonField("labelText", candidate.labelText)
                append(",")
                appendJsonField("confidence", candidate.confidence)
                append(",")
                appendJsonField("score", candidate.score)
                append(",")
                appendJsonField("matchedLabel", candidate.matchedLabel)
                append(",\"supportingLabels\":")
                append(candidate.supportingLabels.toJsonArray { label -> label.jsonQuoted() })
                append(",")
                appendJsonField("context", candidate.context)
                append(",")
                appendJsonField("contextBoost", candidate.contextBoost)
                append(",")
                appendJsonField("riskPenalty", candidate.riskPenalty)
                append(",")
                appendJsonField("requiresConfirmation", candidate.requiresConfirmation)
                append("}")
            }
        })
        append(",\"reasons\":")
        append(reasons.toJsonArray { reason -> reason.jsonQuoted() })
        objectDetection?.let { diagnostics ->
            append(",\"objectDetection\":")
            append(diagnostics.toJson())
        }
        append("}")
    }
}

internal class DiscoveryDiagnosticsHistory(
    private val rawEvents: List<String> = emptyList(),
    private val maxEvents: Int = MaxDiscoveryDiagnosticEvents,
) {
    fun add(event: DiscoveryDiagnosticEvent): DiscoveryDiagnosticsHistory =
        DiscoveryDiagnosticsHistory((rawEvents + event.toJson()).trimToMaxEvents(), maxEvents)

    fun rawEntries(): List<String> = rawEvents.sortedBy { it.timestampMillisFromJson() }

    fun eventCount(): Int = rawEntries().size

    fun exportJson(): String = rawEntries().joinToString(
        prefix = "[",
        postfix = "]",
        separator = ",",
    )

    fun exportText(): String = buildString {
        val entries = rawEntries()
        appendLine("CatchLingo ML Diagnostics")
        appendLine("Events: ${entries.size}")
        appendLine()
        appendLine("Decisions:")
        entries.countFieldValues("decision").forEach { (value, count) ->
            appendLine("- $value: $count")
        }
        appendLine()
        appendLine("Top ML labels:")
        entries.countFieldValues("text").take(12).forEach { (value, count) ->
            appendLine("- $value: $count")
        }
        appendLine()
        appendLine("Top mapped candidates:")
        entries.countFieldValues("id").take(12).forEach { (value, count) ->
            appendLine("- $value: $count")
        }
        appendLine()
        appendLine("Object detection:")
        val objectCountBuckets = entries.countNumericFieldValues("objectCount")
        if (objectCountBuckets.isEmpty()) {
            appendLine("- no object metadata")
        } else {
            appendLine("- selected target exists: ${entries.countSelectedObjectTargets()}/${entries.size}")
            entries.averageNumericFieldValue("areaRatio")?.let { averageArea ->
                appendLine("- average selected area ratio: ${String.format(Locale.US, "%.4f", averageArea)}")
            }
            appendLine("- object count buckets:")
            objectCountBuckets.forEach { (value, count) ->
                appendLine("  - $value: $count")
            }
        }
        appendLine()
        appendLine("Raw JSON:")
        append(exportJson())
    }

    private fun List<String>.trimToMaxEvents(): List<String> =
        sortedBy { it.timestampMillisFromJson() }.takeLast(maxEvents)
}

internal class DiscoveryDiagnosticsRepository(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun addEvent(event: DiscoveryDiagnosticEvent) {
        val updated = loadHistory().add(event)
        preferences.edit()
            .putStringSet(EVENTS_KEY, updated.rawEntries().toSet())
            .apply()
        Log.d(ML_LOG_TAG, "diagnostic saved decision=${event.decision.name} candidate=${event.finalCandidateId ?: event.proposedCandidateId ?: "none"}")
    }

    fun exportJson(): String {
        val json = loadHistory().exportJson()
        Log.d(ML_LOG_TAG, "diagnostic export events=${eventCount()}")
        return json
    }

    fun exportText(): String {
        val text = loadHistory().exportText()
        Log.d(ML_LOG_TAG, "diagnostic export text events=${eventCount()}")
        return text
    }

    fun eventCount(): Int = loadHistory().eventCount()

    private fun loadHistory(): DiscoveryDiagnosticsHistory =
        DiscoveryDiagnosticsHistory(preferences.getStringSet(EVENTS_KEY, emptySet()).orEmpty().toList())

    private companion object {
        const val PREFERENCES_NAME = "catchlingo_ml_diagnostics"
        const val EVENTS_KEY = "events"
    }
}

internal fun DiscoveryCandidate.toDiagnosticCandidate(): DiscoveryDiagnosticCandidate =
    DiscoveryDiagnosticCandidate(
        id = match.id,
        word = match.word,
        source = match.source,
        category = match.category,
        labelText = labelText,
        confidence = confidence,
        score = score,
        matchedLabel = matchedLabel,
        supportingLabels = supportingLabels,
        context = context.name,
        contextBoost = contextBoost,
        riskPenalty = riskPenalty,
        requiresConfirmation = requiresConfirmation,
    )

internal fun buildDiagnosticEvent(
    timestampMillis: Long,
    labels: List<MlLabelObservation>,
    candidates: List<DiscoveryCandidate>,
    decision: DiscoveryDiagnosticDecision,
    proposedCandidateId: String? = null,
    finalCandidateId: String? = null,
    selectedCandidateId: String? = null,
    reasons: List<String> = emptyList(),
    objectDetection: ObjectDetectionDiagnostics? = null,
): DiscoveryDiagnosticEvent = DiscoveryDiagnosticEvent(
    timestampMillis = timestampMillis,
    labels = labels.take(MaxDiagnosticLabels),
    candidates = candidates
        .distinctBy { it.match.id }
        .take(MaxDiagnosticCandidates)
        .map { it.toDiagnosticCandidate() },
    proposedCandidateId = proposedCandidateId,
    finalCandidateId = finalCandidateId,
    selectedCandidateId = selectedCandidateId,
    decision = decision,
    reasons = reasons,
    objectDetection = objectDetection,
)

internal fun buildAlreadyKnownDiagnosticEvent(
    timestampMillis: Long,
    word: de.valentinho13.catchlingo.data.DiscoveredWord,
    reasons: List<String>,
): DiscoveryDiagnosticEvent = DiscoveryDiagnosticEvent(
    timestampMillis = timestampMillis,
    labels = emptyList(),
    candidates = emptyList(),
    proposedCandidateId = word.id,
    finalCandidateId = word.id,
    selectedCandidateId = null,
    decision = DiscoveryDiagnosticDecision.AlreadyKnown,
    reasons = reasons,
)

private fun ObjectDetectionDiagnostics.toJson(): String = buildString {
    append("{")
    appendJsonField("objectCount", objectCount)
    append(",")
    appendJsonField("frameWidth", frameWidth)
    append(",")
    appendJsonField("frameHeight", frameHeight)
    append(",")
    appendJsonField("selectionReason", selectionReason.name.lowercase(Locale.US))
    append(",")
    appendJsonField("hasSelectedTarget", selected != null)
    selected?.let { target ->
        append(",")
        appendJsonField("centerDistance", target.centerDistance)
        append(",")
        appendJsonField("areaRatio", target.areaRatio)
        append(",")
        appendJsonField("hasCategoryLabels", target.hasCategoryLabels)
        append(",\"box\":")
        append(target.box.toJson())
    }
    append("}")
}

private fun NormalizedObjectBox.toJson(): String = buildString {
    append("{")
    appendJsonField("left", left)
    append(",")
    appendJsonField("top", top)
    append(",")
    appendJsonField("right", right)
    append(",")
    appendJsonField("bottom", bottom)
    append("}")
}

private fun String.timestampMillisFromJson(): Long {
    val marker = "\"timestampMillis\":"
    val start = indexOf(marker)
    if (start < 0) return 0L
    val numberStart = start + marker.length
    val numberEnd = indexOf(',', numberStart).takeIf { it > numberStart } ?: indexOf('}', numberStart)
    if (numberEnd <= numberStart) return 0L
    return substring(numberStart, numberEnd).toLongOrNull() ?: 0L
}

private fun List<String>.countFieldValues(fieldName: String): List<Pair<String, Int>> {
    val regex = Regex("\"${Regex.escape(fieldName)}\":\"([^\"]+)\"")
    return flatMap { entry ->
        regex.findAll(entry).map { match -> match.groupValues[1] }.toList()
    }
        .groupingBy { it }
        .eachCount()
        .entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
        .map { it.key to it.value }
}

private fun List<String>.countNumericFieldValues(fieldName: String): List<Pair<String, Int>> {
    val regex = Regex("\"${Regex.escape(fieldName)}\":(-?\\d+)")
    return flatMap { entry ->
        regex.findAll(entry).map { match -> match.groupValues[1] }.toList()
    }
        .groupingBy { it }
        .eachCount()
        .entries
        .sortedWith(compareBy<Map.Entry<String, Int>> { it.key.toIntOrNull() ?: Int.MAX_VALUE })
        .map { it.key to it.value }
}

private fun List<String>.countSelectedObjectTargets(): Int =
    count { it.contains("\"hasSelectedTarget\":true") }

private fun List<String>.averageNumericFieldValue(fieldName: String): Double? {
    val regex = Regex("\"${Regex.escape(fieldName)}\":(-?\\d+(?:\\.\\d+)?)")
    val values = flatMap { entry ->
        regex.findAll(entry).mapNotNull { match -> match.groupValues[1].toDoubleOrNull() }.toList()
    }
    return values.takeIf { it.isNotEmpty() }?.average()
}

private fun StringBuilder.appendJsonField(name: String, value: String?) {
    append("\"")
    append(name)
    append("\":")
    append(value?.jsonQuoted() ?: "null")
}

private fun StringBuilder.appendJsonField(name: String, value: Long) {
    append("\"")
    append(name)
    append("\":")
    append(value)
}

private fun StringBuilder.appendJsonField(name: String, value: Int) {
    append("\"")
    append(name)
    append("\":")
    append(value)
}

private fun StringBuilder.appendJsonField(name: String, value: Float) {
    append("\"")
    append(name)
    append("\":")
    append(String.format(Locale.US, "%.4f", value))
}

private fun StringBuilder.appendJsonField(name: String, value: Boolean) {
    append("\"")
    append(name)
    append("\":")
    append(value)
}

private fun String.jsonQuoted(): String = buildString {
    append("\"")
    this@jsonQuoted.forEach { char ->
        when (char) {
            '\\' -> append("\\\\")
            '"' -> append("\\\"")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> append(char)
        }
    }
    append("\"")
}

private fun <T> List<T>.toJsonArray(transform: (T) -> String): String =
    joinToString(prefix = "[", postfix = "]", separator = ",", transform = transform)

internal const val MaxDiscoveryDiagnosticEvents = 250
internal const val ML_LOG_TAG = "CatchLingoML"
private const val MaxDiagnosticLabels = 5
private const val MaxDiagnosticCandidates = 4
