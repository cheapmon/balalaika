package com.github.cheapmon.balalaika.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
        val preferences = context.getSharedPreferences("com.github.cheapmon.balalaika.preferences", Context.MODE_PRIVATE)
        if (preferences.getBoolean("isDatabaseInitialized", false)) {
            return
        } else {
            this.connect().subscribe {
                val categories = CSV(AndroidResourceLoader(context)).getCategories().map { pair ->
                    Category(id = 0, name = pair.first, widget = pair.second)
                }.toTypedArray()
                it.categoryDao().insertAll(*categories)
            }
            preferences.edit().putBoolean("isDatabaseInitialized", true).apply()
        }
    }
}
