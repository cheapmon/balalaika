package com.github.cheapmon.balalaika.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.util.AndroidResourceLoader
import com.github.cheapmon.balalaika.util.CSV

@Database(entities = [
    Category::class,
    Lemma::class,
    LemmaProperty::class,
    Lexeme::class
], version = 1, exportSchema = false)
abstract class BalalaikaDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun lemmaDao(): LemmaDao
    abstract fun lemmaPropertyDao(): LemmaPropertyDao
    abstract fun lexemeDao(): LexemeDao

    companion object {
        private lateinit var instance: BalalaikaDatabase

        fun connect(): BalalaikaDatabase {
            if (!this::instance.isInitialized) {
                throw IllegalStateException("Database not yet initialized")
            }
            return instance
        }

        fun connect(context: Context): BalalaikaDatabase {
            if (!this::instance.isInitialized) {
                instance = Room.databaseBuilder(
                        context,
                        BalalaikaDatabase::class.java,
                        "balalaika"
                ).build()
                val version = this.populate(context)
                Log.i(this::class.java.name, "Initialized Database version $version")
            }
            return instance
        }

        private fun populate(context: Context): Int {
            val preferencesFile = context.resources.getString(R.string.preferences)
            val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)
            val csv = CSV(AndroidResourceLoader(context))
            if (csv.getVersion() > preferences.getInt("db_version", -1)) {
                instance.clearAllTables()
                instance.categoryDao().insertAll(*csv.getCategories())
                instance.lemmaDao().insertAll(*csv.getLemmata())
                instance.lemmaPropertyDao().insertAll(*csv.getLemmaProperties())
                instance.lexemeDao().insertAll(*csv.getLexemes())
                preferences.edit().putInt("db_version", csv.getVersion()).apply()
                Log.i(this::class.java.name, "Inserted ${instance.categoryDao().count()} categories")
                Log.i(this::class.java.name, "Inserted ${instance.lemmaDao().count()} lemmata")
                Log.i(this::class.java.name, "Inserted ${instance.lemmaPropertyDao().count()} lemma properties")
                Log.i(this::class.java.name, "Inserted ${instance.lexemeDao().count()} lexemes")
            }
            return csv.getVersion()
        }
    }
}
