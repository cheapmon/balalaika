package com.github.cheapmon.balalaika.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.util.AndroidResourceLoader
import com.github.cheapmon.balalaika.util.CSV
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

@Database(entities = [Category::class], version = 1)
abstract class BalalaikaDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}

object DB {
    private lateinit var db: BalalaikaDatabase
    lateinit var connection: Observable<BalalaikaDatabase>

    fun init(context: Context) {
        db = Room.databaseBuilder(context, BalalaikaDatabase::class.java, "balalaika").build()
        this.populate(context)
    }

    fun connect(): Observable<BalalaikaDatabase> {
        return Observable.just(db).subscribeOn(Schedulers.io())
    }

    private fun populate(context: Context) {
        val preferencesFile = context.resources.getString(R.string.preferences)
        val dbInitKey = context.resources.getString(R.string.db_init_key)
        val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)
        if (!preferences.getBoolean(dbInitKey, false)) {
            this.connect().subscribe {
                val categories = CSV(AndroidResourceLoader(context)).getCategories()
                it.categoryDao().insertAll(*categories)
            }
            preferences.edit().putBoolean("isDatabaseInitialized", true).apply()
        }
    }
}
