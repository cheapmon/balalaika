package com.github.cheapmon.balalaika.db

import androidx.room.*

@Entity
data class Word(
        @PrimaryKey val id: String
)

@Dao
interface WordDao {
    @Query("SELECT * FROM word")
    fun getAll(): List<Word>

    @Query("SELECT * FROM word WHERE id = (:id) LIMIT 1")
    fun findById(id: String): Word?

    @Query("SELECT count(*) FROM word")
    fun count(): Int

    @Insert
    fun insertAll(vararg categories: Word)
}