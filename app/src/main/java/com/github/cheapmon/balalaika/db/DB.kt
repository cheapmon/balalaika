package com.github.cheapmon.balalaika.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.util.AndroidResourceLoader
import com.github.cheapmon.balalaika.util.CSV
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

@Database(entities = [Category::class, Lemma::class, LemmaValue::class], version = 1, exportSchema = false)
abstract class BalalaikaDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun lemmaDao(): LemmaDao
    abstract fun lemmaValueDao(): LemmaValueDao
}

object DB {
    private lateinit var db: BalalaikaDatabase
    lateinit var connection: Observable<BalalaikaDatabase>

    fun init(context: Context): Observable<BalalaikaDatabase> {
        db = Room.databaseBuilder(context, BalalaikaDatabase::class.java, "balalaika").build()
        return this.populate(context)
    }

    private fun connect(): Observable<BalalaikaDatabase> {
        return Observable.just(db).subscribeOn(Schedulers.io())
    }

    private fun populate(context: Context): Observable<BalalaikaDatabase> {
        val preferencesFile = context.resources.getString(R.string.preferences)
        val dbInitKey = context.resources.getString(R.string.db_init_key)
        val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)
        return if (!preferences.getBoolean(dbInitKey, false)) {
            this.connect().map {
                val csv = CSV(AndroidResourceLoader(context))
                it.categoryDao().insertAll(*csv.getCategories())
                Log.i(this::class.java.name, "Inserted ${it.categoryDao().count()} categories")
                it.lemmaDao().insertAll(*csv.getWords())
                Log.i(this::class.java.name, "Inserted ${it.lemmaDao().count()} lemmata")
                it.lemmaValueDao().insertAll(*csv.getWordInfos())
                Log.i(this::class.java.name, "Inserted ${it.lemmaValueDao().count()} tags")
                preferences.edit().putBoolean(dbInitKey, true).apply()
                it
            }
        } else {
            this.connect()
        }
    }
}
