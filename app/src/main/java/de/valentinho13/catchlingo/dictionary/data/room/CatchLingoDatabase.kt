package de.valentinho13.catchlingo.dictionary.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import de.valentinho13.catchlingo.dictionary.data.room.entity.CatchEventEntity
import de.valentinho13.catchlingo.dictionary.data.room.entity.ReviewStateEntity
import de.valentinho13.catchlingo.dictionary.data.room.entity.WordEntity

@Database(
    entities = [WordEntity::class, CatchEventEntity::class, ReviewStateEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class CatchLingoDatabase : RoomDatabase() {
    abstract fun dictionaryDao(): DictionaryDao
}
