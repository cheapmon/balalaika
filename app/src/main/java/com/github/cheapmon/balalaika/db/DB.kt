package com.github.cheapmon.balalaika.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.util.AndroidResourceLoader
import com.github.cheapmon.balalaika.util.CSV

@Database(entities = [
    Category::class,
    Lexeme::class,
    LexemeProperty::class,
    FullForm::class
], version = 1, exportSchema = false)
abstract class BalalaikaDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun lexemeDao(): LexemeDao
    abstract fun lexemePropertyDao(): LexemePropertyDao
    abstract fun fullFormDao(): FullFormDao

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
                clearTables(db, arrayOf("category", "lexeme", "lexeme_property", "full_form"))
                insert(db, "category", csv.getCategories())
                insert(db, "lexeme", csv.getLexemes())
                insert(db, "lexeme_property", csv.getLexemeProperties())
                insert(db, "full_form", csv.getFullForms())
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
