package de.valentinho13.catchlingo.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DiscoveryRepository(context: Context) {
    private val preferences = context.getSharedPreferences("catchlingo_discoveries", Context.MODE_PRIVATE)
    private val _words = MutableStateFlow(loadWords())

    val words: StateFlow<List<DiscoveredWord>> = _words.asStateFlow()

    fun collectWord(word: DiscoveredWord): Boolean {
        if (_words.value.any { it.id == word.id }) {
            return false
        }
        val updated = (_words.value + word).sortedByDescending { it.discoveredAtMillis }
        preferences.edit()
            .putStringSet(WORDS_KEY, updated.map { it.encode() }.toSet())
            .apply()
        _words.value = updated
        return true
    }

    private fun loadWords(): List<DiscoveredWord> = preferences
        .getStringSet(WORDS_KEY, emptySet())
        .orEmpty()
        .mapNotNull { it.decodeWord() }
        .sortedByDescending { it.discoveredAtMillis }

    private fun DiscoveredWord.encode(): String = listOf(
        id,
        word,
        source,
        category,
        discoveredAtMillis.toString(),
    ).joinToString(DELIMITER)

    private fun String.decodeWord(): DiscoveredWord? {
        val parts = split(DELIMITER)
        if (parts.size != 5) return null
        return DiscoveredWord(
            id = parts[0],
            word = parts[1],
            source = parts[2],
            category = parts[3],
            discoveredAtMillis = parts[4].toLongOrNull() ?: return null,
        )
    }

    private companion object {
        const val WORDS_KEY = "words"
        const val DELIMITER = "|"
    }
}
