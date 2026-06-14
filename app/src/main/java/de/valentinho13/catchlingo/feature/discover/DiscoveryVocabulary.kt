package de.valentinho13.catchlingo.feature.discover

import de.valentinho13.catchlingo.data.DiscoveredWord

internal data class VocabularyMatch(
    val id: String,
    val word: String,
    val source: String,
    val category: String,
)

internal fun mapLabelToVocabulary(label: String): VocabularyMatch? {
    val normalized = label.lowercase()
    return Vocabulary.entries.firstOrNull { entry ->
        entry.labels.any { normalized.contains(it) }
    }?.toMatch()
}

internal fun VocabularyMatch.toDiscoveredWord(nowMillis: Long): DiscoveredWord = DiscoveredWord(
    id = id,
    word = word,
    source = source,
    category = category,
    discoveredAtMillis = nowMillis,
)

private enum class Vocabulary(
    val id: String,
    val word: String,
    val source: String,
    val category: String,
    val labels: Set<String>,
) {
    Coffee(
        id = "kopi",
        word = "kopi",
        source = "coffee",
        category = "Essen & Trinken",
        labels = setOf("coffee", "cup", "mug", "drinkware"),
    ),
    Chair(
        id = "kursi",
        word = "kursi",
        source = "chair",
        category = "Zuhause",
        labels = setOf("chair", "seat", "stool"),
    ),
    Table(
        id = "meja",
        word = "meja",
        source = "table",
        category = "Zuhause",
        labels = setOf("table", "desk"),
    ),
    Bicycle(
        id = "sepeda",
        word = "sepeda",
        source = "bicycle",
        category = "Unterwegs",
        labels = setOf("bicycle", "bike", "cycling"),
    ),
    Book(
        id = "buku",
        word = "buku",
        source = "book",
        category = "Zuhause",
        labels = setOf("book", "notebook"),
    ),
    Bottle(
        id = "botol",
        word = "botol",
        source = "bottle",
        category = "Essen & Trinken",
        labels = setOf("bottle", "water bottle"),
    ),
    Plant(
        id = "tanaman",
        word = "tanaman",
        source = "plant",
        category = "Natur",
        labels = setOf("plant", "houseplant", "flower"),
    ),
    Phone(
        id = "ponsel",
        word = "ponsel",
        source = "phone",
        category = "Unterwegs",
        labels = setOf("phone", "mobile phone", "telephone"),
    ),
    Laptop(
        id = "laptop",
        word = "laptop",
        source = "laptop",
        category = "Zuhause",
        labels = setOf("laptop", "computer"),
    ),
    Bag(
        id = "tas",
        word = "tas",
        source = "bag",
        category = "Unterwegs",
        labels = setOf("bag", "handbag", "backpack"),
    );

    fun toMatch(): VocabularyMatch = VocabularyMatch(
        id = id,
        word = word,
        source = source,
        category = category,
    )
}
