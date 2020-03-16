package com.github.cheapmon.balalaika.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.cheapmon.balalaika.data.entities.*

@Database(
    entities = [
        Category::class,
        Lexeme::class,
        Property::class,
        DictionaryView::class,
        DictionaryViewToCategory::class,
        HistoryEntry::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DB : RoomDatabase() {
    abstract fun categories(): CategoryDao
    abstract fun lexemes(): LexemeDao
    abstract fun properties(): PropertyDao
    abstract fun dictionaryViews(): DictionaryViewDao
    abstract fun historyEntries(): HistoryEntryDao

    companion object {
        @Volatile
        private var instance: DB? = null

        fun getInstance(context: Context): DB {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context, DB::class.java, "balalaika").build()
            }
        }
    }
}
