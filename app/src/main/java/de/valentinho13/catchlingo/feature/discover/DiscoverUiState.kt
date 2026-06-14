package de.valentinho13.catchlingo.feature.discover

import androidx.compose.runtime.Immutable

@Immutable
data class DiscoverUiState(
    val greeting: String,
    val companionLine: String,
    val wordsToday: Int,
    val sessionWords: Int,
    val rememberedWords: Int,
    val streakDays: Int,
    val sessions: Int,
    val sceneTitle: String,
    val noticedWords: List<NoticedWord>,
    val freshFinds: List<SpecimenWord>,
    val categories: List<DiscoverCategory>,
)

val EmptyDiscoverState = DiscoverUiState(
    greeting = "Guten Morgen!",
    companionLine = "Richte deinen Blick auf die Welt und sammle den ersten Fund.",
    wordsToday = 0,
    sessionWords = 0,
    rememberedWords = 0,
    streakDays = 0,
    sessions = 0,
    sceneTitle = "Sonnenlicht am Cafétisch",
    noticedWords = listOf(
        NoticedWord(word = "kopi", source = "coffee", x = 0.28f, y = 0.58f),
        NoticedWord(word = "kursi", source = "chair", x = 0.72f, y = 0.28f),
        NoticedWord(word = "meja", source = "table", x = 0.25f, y = 0.78f),
        NoticedWord(word = "sepeda", source = "bicycle", x = 0.74f, y = 0.66f),
    ),
    freshFinds = emptyList(),
    categories = emptyList(),
)

@Immutable
data class NoticedWord(
    val word: String,
    val source: String,
    val x: Float,
    val y: Float,
)

@Immutable
data class SpecimenWord(
    val word: String,
    val source: String,
    val context: String,
    val status: String,
)

@Immutable
data class DiscoverCategory(
    val label: String,
    val count: Int,
    val selected: Boolean = false,
)

val PreviewDiscoverState = DiscoverUiState(
    greeting = "Guten Morgen!",
    companionLine = "Die Welt wartet schon auf neue Wörter.",
    wordsToday = 12,
    sessionWords = 4,
    rememberedWords = 8,
    streakDays = 3,
    sessions = 5,
    sceneTitle = "Sonnenlicht am Cafétisch",
    noticedWords = listOf(
        NoticedWord(word = "kopi", source = "coffee", x = 0.28f, y = 0.58f),
        NoticedWord(word = "kursi", source = "chair", x = 0.72f, y = 0.28f),
        NoticedWord(word = "meja", source = "table", x = 0.25f, y = 0.78f),
        NoticedWord(word = "sepeda", source = "bicycle", x = 0.74f, y = 0.66f),
    ),
    freshFinds = listOf(
        SpecimenWord(word = "kopi", source = "coffee", context = "Essen & Trinken", status = "Neu"),
        SpecimenWord(word = "meja", source = "table", context = "Zuhause", status = "Wiedergesehen"),
    ),
    categories = listOf(
        DiscoverCategory("Heute", 12, selected = true),
        DiscoverCategory("Zuhause", 6),
        DiscoverCategory("Unterwegs", 4),
        DiscoverCategory("Natur", 3),
    ),
)
