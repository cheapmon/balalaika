package com.github.cheapmon.balalaika.db

import android.content.Context
import androidx.room.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

@Entity
data class Category(
        @PrimaryKey val uid: Int,
        @ColumnInfo(name = "name") val name: String?,
        @ColumnInfo(name = "widget") val widget: String?
)

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): List<Category>

    @Query("SELECT * FROM category WHERE name like (:name) LIMIT 1")
    fun findByName(name: String): Category?
}

@Database(entities = [Category::class], version = 1)
abstract class BalalaikaDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}

object DB {
    private lateinit var db: BalalaikaDatabase
    lateinit var connection: Observable<BalalaikaDatabase>

    fun init(context: Context) {
        db = Room.databaseBuilder(context, BalalaikaDatabase::class.java, "balalaika").build()
    }

    fun connect(): Observable<BalalaikaDatabase> {
        return Observable.just(db).subscribeOn(Schedulers.io())
    }
}
