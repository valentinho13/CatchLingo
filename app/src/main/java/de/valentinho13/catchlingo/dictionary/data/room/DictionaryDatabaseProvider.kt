package de.valentinho13.catchlingo.dictionary.data.room

import android.content.Context
import androidx.room.Room

object DictionaryDatabaseProvider {
    @Volatile
    private var instance: CatchLingoDatabase? = null

    fun get(context: Context): CatchLingoDatabase = instance ?: synchronized(this) {
        instance ?: Room.databaseBuilder(
            context.applicationContext,
            CatchLingoDatabase::class.java,
            "catchlingo.db",
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
            .also { instance = it }
    }
}
