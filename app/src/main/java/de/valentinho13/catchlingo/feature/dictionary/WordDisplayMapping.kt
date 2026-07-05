package de.valentinho13.catchlingo.feature.dictionary

import de.valentinho13.catchlingo.data.DiscoveredWord
import de.valentinho13.catchlingo.dictionary.model.Word
import de.valentinho13.catchlingo.feature.discover.findVocabularyById
import de.valentinho13.catchlingo.feature.discover.toDiscoveredWord

internal fun Word.toDisplayWordOrNull(): DiscoveredWord? =
    findVocabularyById(label)?.toDiscoveredWord(createdAt)
