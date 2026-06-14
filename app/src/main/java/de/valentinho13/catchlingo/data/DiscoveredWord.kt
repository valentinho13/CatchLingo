package de.valentinho13.catchlingo.data

import androidx.compose.runtime.Immutable

@Immutable
data class DiscoveredWord(
    val id: String,
    val word: String,
    val source: String,
    val category: String,
    val discoveredAtMillis: Long,
)
