package de.valentinho13.catchlingo.feature.dictionary

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.valentinho13.catchlingo.core.time.SystemTimeProvider
import de.valentinho13.catchlingo.core.time.TimeProvider
import de.valentinho13.catchlingo.data.DiscoveredWord
import de.valentinho13.catchlingo.dictionary.data.DictionaryRepository
import de.valentinho13.catchlingo.dictionary.data.room.DictionaryDatabaseProvider
import de.valentinho13.catchlingo.dictionary.data.room.RoomDictionaryRepository
import de.valentinho13.catchlingo.discover.model.Candidate
import de.valentinho13.catchlingo.feature.discover.VocabularyMatch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DictionaryViewModel(
    private val repository: DictionaryRepository,
    private val time: TimeProvider = SystemTimeProvider(),
) : ViewModel() {
    val words: StateFlow<List<DiscoveredWord>> = repository.observeDictionary(TargetLanguage)
        .map { domainWords ->
            domainWords.mapNotNull { it.toDisplayWordOrNull() }
                .sortedByDescending(DiscoveredWord::discoveredAtMillis)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    suspend fun confirm(match: VocabularyMatch): Boolean = confirm(match, confidence = Float.NaN)

    suspend fun confirm(match: VocabularyMatch, confidence: Float): Boolean {
        val now = time.nowMillis()
        return repository.confirm(
            candidate = Candidate(
                id = "${match.id}-$now",
                rawLabel = match.source,
                normalizedLabel = match.id,
                confidence = confidence,
                source = "discovery-vocabulary",
                detectedAt = now,
            ),
            targetLanguage = TargetLanguage,
        ).isNew
    }

    companion object {
        private const val TargetLanguage = "id"

        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val time = SystemTimeProvider()
                val database = DictionaryDatabaseProvider.get(context.applicationContext)
                return DictionaryViewModel(RoomDictionaryRepository(database, time), time) as T
            }
        }
    }
}
