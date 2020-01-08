package com.github.cheapmon.balalaika.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.util.AndroidResourceLoader
import com.github.cheapmon.balalaika.util.CSV
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
            GlobalScope.launch {
                init()
            }
        }

        private fun init() {
            val preferencesFile = context.resources.getString(R.string.preferences)
            val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)
            val csv = CSV(AndroidResourceLoader(context))
            if (csv.getVersion() > preferences.getInt("db_version", -1)) {
                populate(csv)
                preferences.edit().putInt("db_version", csv.getVersion()).apply()
            }
            Log.i(this::class.java.name, "Initialized Database version ${preferences.getInt("db_version", -1)}")
        }

        private fun populate(csv: CSV) {
            instance.clearAllTables()
            instance.categoryDao().insertAll(*csv.getCategories())
            instance.lexemeDao().insertAll(*csv.getLexemes())
            instance.lexemePropertyDao().insertAll(*csv.getLexemeProperties())
            instance.fullFormDao().insertAll(*csv.getFullForms())
            Log.i(this::class.java.name, "Inserted ${instance.categoryDao().count()} categories")
            Log.i(this::class.java.name, "Inserted ${instance.lexemeDao().count()} lexemes")
            Log.i(this::class.java.name, "Inserted ${instance.lexemePropertyDao().count()} lexeme properties")
            Log.i(this::class.java.name, "Inserted ${instance.fullFormDao().count()} full forms")
        }
    }
}
