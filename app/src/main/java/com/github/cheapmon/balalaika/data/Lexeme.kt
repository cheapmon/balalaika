package com.github.cheapmon.balalaika.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class Lexeme(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val lexemeId: Long = 0,
    val form: String
)

@Dao
interface LexemeDao {
    @Query("SELECT * FROM lexeme")
    fun getAll(): Flow<List<Lexeme>>

    @Query("SELECT COUNT(*) FROM lexeme")
    fun count(): Flow<Int>

    @Insert
    suspend fun insertAll(vararg lexemes: Lexeme)
}
