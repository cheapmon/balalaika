package com.github.cheapmon.balalaika.db

import androidx.room.*

@Entity
data class Category(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "external_id") val externalId: String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "widget") val widget: String
)

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): List<Category>

    @Query("SELECT * FROM category WHERE name like (:name) LIMIT 1")
    fun findByName(name: String): Category?

    @Query("SELECT count(*) FROM category")
    fun count(): Int

    @Insert
    fun insertAll(vararg categories: Category)
}