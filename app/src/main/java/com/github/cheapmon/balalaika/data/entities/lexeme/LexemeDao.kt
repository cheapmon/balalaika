package com.github.cheapmon.balalaika.data.entities.lexeme

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface LexemeDao {
    @Query("SELECT * FROM lexeme WHERE is_bookmark = 1")
    fun getBookmarks(): Flow<List<Lexeme>>

    @Query("SELECT * FROM lexeme WHERE id = (:id) LIMIT 1")
    fun findById(id: Long): Flow<Lexeme?>

    @Query("SELECT * FROM lexeme WHERE form LIKE '%' || (:query) || '%'")
    fun findByForm(query: String): Flow<List<Lexeme>>

    @Query("UPDATE lexeme SET is_bookmark = NOT is_bookmark WHERE id = (:id)")
    suspend fun toggleBookmark(id: Long)

    @Query("UPDATE lexeme SET is_bookmark = 0 WHERE is_bookmark = 1")
    suspend fun clearBookmarks()

    @Insert
    suspend fun insertAll(vararg lexemes: Lexeme)
}