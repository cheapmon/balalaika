package com.github.cheapmon.balalaika.db

import androidx.room.*

@Entity
data class Lexeme(
        @PrimaryKey val id: String
)

@Dao
interface LexemeDao {
    @Query("SELECT * FROM lexeme")
    fun getAll(): List<Lexeme>

    @Query("SELECT * FROM lexeme WHERE id = (:id) LIMIT 1")
    fun findById(id: String): Lexeme?

    @Query("SELECT count(*) FROM lexeme")
    fun count(): Int

    @Insert
    fun insertAll(vararg lexemes: Lexeme)
}