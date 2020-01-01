package com.github.cheapmon.balalaika.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class Category(
        @PrimaryKey val id: String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "widget") val widget: String
)

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): List<Category>

    @Query("SELECT * FROM category WHERE id = (:id) LIMIT 1")
    fun findById(id: String): Category?

    @Query("SELECT count(*) FROM category")
    fun count(): Int

    @Insert
    fun insertAll(vararg categories: Category)
}