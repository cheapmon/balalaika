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
    LemmaValue::class,
    Lexeme::class
], version = 1, exportSchema = false)
abstract class BalalaikaDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun lemmaDao(): LemmaDao
    abstract fun lemmaValueDao(): LemmaValueDao
    abstract fun lexemeValueDao(): LexemeDao

    companion object {
        private lateinit var instance: BalalaikaDatabase

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
            val dbVersion = preferences.getInt("db_version", -1)
            if (csv.getVersion() > preferences.getInt("db_version", -1)) {
                instance.clearAllTables()
                instance.categoryDao().insertAll(*csv.getCategories())
                instance.lemmaDao().insertAll(*csv.getLemmata())
                instance.lemmaValueDao().insertAll(*csv.getLemmaValues())
                instance.lexemeValueDao().insertAll(*csv.getLexemes())
                preferences.edit().putInt("db_version", csv.getVersion()).apply()
                Log.i(this::class.java.name, "Inserted ${instance.categoryDao().count()} categories")
                Log.i(this::class.java.name, "Inserted ${instance.lemmaDao().count()} lemmata")
                Log.i(this::class.java.name, "Inserted ${instance.lemmaValueDao().count()} lemma values")
                Log.i(this::class.java.name, "Inserted ${instance.lexemeValueDao().count()} lexemes")
            }
            return csv.getVersion()
        }
    }
}
