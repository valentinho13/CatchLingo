package de.valentinho13.catchlingo.dictionary.data.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Room-Abbild von Word. Getrennt vom reinen Domain-Modell. */
@Entity(
    tableName = "words",
    indices = [Index(value = ["label", "targetLanguage"], unique = true)],
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val targetLanguage: String,
    val translation: String?,
    val createdAt: Long,
)
