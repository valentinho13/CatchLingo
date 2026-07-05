package de.valentinho13.catchlingo.dictionary.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room-Abbild von CatchEvent. `resolvedWordId` ist nullable (REJECTED-Events ohne Word).
 * Mehrere Events pro Word sind erlaubt (kein Unique auf resolvedWordId). Status als String
 * gespeichert und im Mapper auf CatchStatus abgebildet (kein TypeConverter nötig).
 */
@Entity(
    tableName = "catch_events",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["resolvedWordId"],
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [Index("resolvedWordId")],
)
data class CatchEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val confidence: Float,
    val occurredAt: Long,
    val resolvedWordId: Long?,
    val status: String,
)
