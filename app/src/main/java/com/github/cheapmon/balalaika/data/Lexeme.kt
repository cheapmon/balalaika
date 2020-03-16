package com.github.cheapmon.balalaika.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class Lexeme(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val lexemeId: Long = 0,
        @ColumnInfo(name = "form") val form: String,
        @ColumnInfo(name = "base_id") val baseId: Long?
)

data class LexemeWithProperties(
        @Embedded val lexeme: Lexeme,
        @Relation(parentColumn = "id", entityColumn = "lexeme_id") val properties: List<Property>
)

@Dao
interface LexemeDao {
    @Query("SELECT * FROM lexeme")
    fun getAll(): Flow<List<Lexeme>>

    @Transaction
    @Query("SELECT * FROM lexeme")
    fun getAllWithProperties(): Flow<List<LexemeWithProperties>>

    @Query("SELECT COUNT(*) FROM lexeme")
    fun count(): Flow<Int>

    @Insert
    suspend fun insertAll(vararg lexemes: Lexeme)
}
