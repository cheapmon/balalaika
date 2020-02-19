package com.github.cheapmon.balalaika.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.util.AndroidResourceLoader
import com.github.cheapmon.balalaika.util.CSV
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

@Database(entities = [
    Category::class,
    Lexeme::class,
    LexemeProperty::class,
    FullForm::class,
    DictionaryView::class,
    SearchHistoryEntry::class
], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BalalaikaDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun lexemeDao(): LexemeDao
    abstract fun lexemePropertyDao(): LexemePropertyDao
    abstract fun fullFormDao(): FullFormDao
    abstract fun dictionaryViewDao(): DictionaryViewDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        lateinit var instance: BalalaikaDatabase

        fun init(context: Context) {
            instance = Room.databaseBuilder(context, BalalaikaDatabase::class.java, "balalaika")
                    .addCallback(Callback(context))
                    .build()
        }
    }

    private class Callback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val csv = CSV(AndroidResourceLoader(context))
            if (shouldUpdate(csv)) {
                clearTables(db, arrayOf("category", "lexeme", "lexeme_property", "full_form", "dictionary_view"))
                insert(db, "category", csv.getCategories())
                insert(db, "lexeme", csv.getLexemes())
                insert(db, "lexeme_property", csv.getLexemeProperties())
                insert(db, "full_form", csv.getFullForms())
                insert(db, "dictionary_view", csv.getDictionaryViews())
            }
        }

        private fun clearTables(db: SupportSQLiteDatabase, tables: Array<String>) {
            tables.forEach { db.execSQL("DELETE FROM $it") }
        }

        private fun insert(db: SupportSQLiteDatabase, table: String, values: List<ContentValues>) {
            values.forEach { db.insert(table, SQLiteDatabase.CONFLICT_ABORT, it) }
        }

        private fun shouldUpdate(csv: CSV): Boolean {
            val preferencesFile = context.resources.getString(R.string.preferences)
            val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)
            return if (csv.getVersion() > preferences.getInt("db_version", -1)) {
                preferences.edit().putInt("db_version", csv.getVersion()).apply()
                true
            } else false
        }
    }
}
