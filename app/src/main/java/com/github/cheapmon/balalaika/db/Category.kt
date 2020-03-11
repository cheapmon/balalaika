package com.github.cheapmon.balalaika.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(indices = [Index(value = ["sequence"], unique = true)])
data class Category(
        @PrimaryKey val id: String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "widget") val widget: String,
        @ColumnInfo(name = "sequence") val sequence: Int,
        @ColumnInfo(name = "hidden") val hidden: Boolean,
        @ColumnInfo(name = "order_by") val orderBy: Boolean
)

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): List<Category>

    @Query("SELECT * FROM category WHERE order_by = 1")
    fun getOrdered(): List<Category>

    @Query("SELECT * FROM category WHERE id = (:id) LIMIT 1")
    fun findById(id: String): Category?

    @Query("SELECT * FROM category WHERE name = (:name) LIMIT 1")
    fun findByName(name: String): LiveData<Category?>

    @Query("SELECT count(*) FROM category")
    fun count(): Int

    @Insert
    fun insertAll(vararg categories: Category)
}