package com.github.cheapmon.balalaika.db

import androidx.room.*

@Entity
data class Word(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "external_id") val externalId: String
)

@Dao
interface WordDao {
    @Query("SELECT * FROM word")
    fun getAll(): List<Word>

    @Query("SELECT * FROM word WHERE external_id = (:id) LIMIT 1")
    fun findById(id: String): Word?

    @Insert
    fun insertAll(vararg categories: Word)
}