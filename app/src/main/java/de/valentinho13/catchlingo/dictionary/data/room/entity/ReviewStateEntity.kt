package de.valentinho13.catchlingo.dictionary.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/** Room-Abbild von ReviewState, an Word gekoppelt (CASCADE-Delete). */
@Entity(
    tableName = "review_states",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ReviewStateEntity(
    @PrimaryKey val wordId: Long,
    val dueAt: Long,
    val lastReviewedAt: Long?,
)
