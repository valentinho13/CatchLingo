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
    val cropLabeling: CropLabelingDiagnostics? = null,
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
        cropLabeling?.let { diagnostics ->
            append(",\"cropLabeling\":")
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
        appendBenchmarkSummary(entries)
        appendLine()
        appendBenchmarkHints(entries)
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
        appendLine("Crop labeling:")
        val cropAttempts = entries.countCropAttempts()
        if (cropAttempts == 0) {
            appendLine("- no crop metadata")
        } else {
            val cropSuccesses = entries.countCropSuccesses()
            val changedTopLabels = entries.countChangedCropTopLabels()
            appendLine("- attempts: $cropAttempts")
            appendLine("- success rate: ${String.format(Locale.US, "%.2f", cropSuccesses.toFloat() / cropAttempts)}")
            appendLine("- changed top label: $changedTopLabels/$cropSuccesses")
            appendLine("- most common crop labels:")
            entries.countNestedLabelValues("cropLabels").take(8).forEach { (value, count) ->
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

private fun StringBuilder.appendBenchmarkSummary(entries: List<String>) {
    val cropAttempts = entries.countCropAttempts()
    val cropSuccesses = entries.countCropSuccesses()
    val changedTopLabels = entries.countChangedCropTopLabels()
    appendLine("Benchmark summary")
    appendLine("- total events: ${entries.size}")
    appendLine("- whole-frame top labels:")
    entries.countFieldValues("topWholeFrameLabel").appendCountLinesTo(this)
    appendLine("- crop top labels:")
    entries.countFieldValues("topCropLabel").appendCountLinesTo(this)
    appendLine("- object detection targets: ${entries.countSelectedObjectTargets()}/${entries.size}")
    appendLine("- object target reasons:")
    entries.countFieldValues("selectionReason").appendCountLinesTo(this)
    appendLine("- crop success rate: ${cropSuccesses}/${cropAttempts} (${cropSuccesses.rateOf(cropAttempts)})")
    appendLine("- cropTop != wholeFrameTop: $changedTopLabels/$cropSuccesses")
    appendLine("- confirmed words:")
    entries.confirmedWordCounts().appendCountLinesTo(this)
    appendLine("- rejected/none-of-these: ${entries.countDecisions(DiscoveryDiagnosticDecision.UserRejectedNoneOfThese.name)}")
    appendLine("- already-known: ${entries.countDecisions(DiscoveryDiagnosticDecision.AlreadyKnown.name)}")
}

private fun StringBuilder.appendBenchmarkHints(entries: List<String>) {
    appendLine("Benchmark hints")
    appendLine("- recent cropTop != wholeFrameTop:")
    entries.recentWhere(limit = BenchmarkHintLimit) { it.contains("\"didCropChangeTopLabel\":true") }
        .appendEventLinesTo(this)
    appendLine("- recent no candidate but crop labels existed:")
    entries.recentWhere(limit = BenchmarkHintLimit) {
        it.contains("\"candidates\":[]") && it.cropTopLabel() != null
    }.appendEventLinesTo(this)
    appendLine("- recent user-confirmed candidates:")
    entries.recentWhere(limit = BenchmarkHintLimit) {
        it.contains("\"decision\":\"${DiscoveryDiagnosticDecision.UserConfirmed.name}\"")
    }.appendEventLinesTo(this)
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
    cropLabeling: CropLabelingDiagnostics? = null,
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
    cropLabeling = cropLabeling,
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

private fun CropLabelingDiagnostics.toJson(): String = buildString {
    append("{")
    appendJsonField("cropSuccess", cropSuccess)
    append(",")
    appendJsonField("cropFailureReason", cropFailureReason)
    append(",\"wholeFrameLabels\":")
    append(wholeFrameLabels.toJsonArray { label -> label.toJson() })
    append(",\"cropLabels\":")
    append(cropLabels.toJsonArray { label -> label.toJson() })
    append(",\"labelComparison\":")
    append(labelComparison.toJson())
    append("}")
}

private fun LabelComparisonSummary.toJson(): String = buildString {
    append("{")
    appendJsonField("topWholeFrameLabel", topWholeFrameLabel)
    append(",")
    appendJsonField("topCropLabel", topCropLabel)
    append(",")
    appendJsonField("didCropChangeTopLabel", didCropChangeTopLabel)
    append("}")
}

private fun MlLabelObservation.toJson(): String = buildString {
    append("{")
    appendJsonField("text", text)
    append(",")
    appendJsonField("confidence", confidence)
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

private fun List<String>.confirmedWordCounts(): List<Pair<String, Int>> =
    filter { it.contains("\"decision\":\"${DiscoveryDiagnosticDecision.UserConfirmed.name}\"") }
        .mapNotNull { it.stringFieldValue("finalCandidateId") ?: it.stringFieldValue("selectedCandidateId") }
        .groupingBy { it }
        .eachCount()
        .entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
        .map { it.key to it.value }

private fun List<String>.countDecisions(decision: String): Int =
    count { it.contains("\"decision\":\"$decision\"") }

private fun List<String>.recentWhere(limit: Int, predicate: (String) -> Boolean): List<String> =
    filter(predicate).takeLast(limit)

private fun List<Pair<String, Int>>.appendCountLinesTo(builder: StringBuilder) {
    if (isEmpty()) {
        builder.appendLine("  - none")
    } else {
        forEach { (value, count) -> builder.appendLine("  - $value: $count") }
    }
}

private fun List<String>.appendEventLinesTo(builder: StringBuilder) {
    if (isEmpty()) {
        builder.appendLine("  - none")
    } else {
        forEach { entry ->
            builder.appendLine(
                "  - t=${entry.timestampMillisFromJson()} whole=${entry.wholeTopLabel().orEmpty()} " +
                    "crop=${entry.cropTopLabel().orEmpty()} decision=${entry.stringFieldValue("decision").orEmpty()} " +
                    "candidate=${entry.stringFieldValue("finalCandidateId") ?: entry.stringFieldValue("proposedCandidateId") ?: "none"}",
            )
        }
    }
}

private fun Int.rateOf(total: Int): String =
    if (total <= 0) {
        "0.00"
    } else {
        String.format(Locale.US, "%.2f", toFloat() / total)
    }

private fun String.wholeTopLabel(): String? =
    stringFieldValue("topWholeFrameLabel") ?: firstLabelTextFromArray("labels")

private fun String.cropTopLabel(): String? =
    stringFieldValue("topCropLabel")

private fun String.stringFieldValue(fieldName: String): String? {
    val regex = Regex("\"${Regex.escape(fieldName)}\":\"([^\"]+)\"")
    return regex.find(this)?.groupValues?.get(1)
}

private fun String.firstLabelTextFromArray(arrayFieldName: String): String? {
    val arrayRegex = Regex("\"${Regex.escape(arrayFieldName)}\":\\[(.*?)]")
    val labelRegex = Regex("\"text\":\"([^\"]+)\"")
    val arrayBody = arrayRegex.find(this)?.groupValues?.get(1) ?: return null
    return labelRegex.find(arrayBody)?.groupValues?.get(1)
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

private fun List<String>.countCropAttempts(): Int =
    count { it.contains("\"cropLabeling\":") }

private fun List<String>.countCropSuccesses(): Int =
    count { it.contains("\"cropSuccess\":true") }

private fun List<String>.countChangedCropTopLabels(): Int =
    count { it.contains("\"didCropChangeTopLabel\":true") }

private fun List<String>.countNestedLabelValues(arrayFieldName: String): List<Pair<String, Int>> {
    val arrayRegex = Regex("\"${Regex.escape(arrayFieldName)}\":\\[(.*?)]")
    val labelRegex = Regex("\"text\":\"([^\"]+)\"")
    return flatMap { entry ->
        arrayRegex.findAll(entry).flatMap { arrayMatch ->
            labelRegex.findAll(arrayMatch.groupValues[1]).map { labelMatch -> labelMatch.groupValues[1] }
        }.toList()
    }
        .groupingBy { it }
        .eachCount()
        .entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
        .map { it.key to it.value }
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
private const val BenchmarkHintLimit = 8
