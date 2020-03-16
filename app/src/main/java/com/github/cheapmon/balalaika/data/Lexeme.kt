package com.github.cheapmon.balalaika.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(
    foreignKeys = [
        ForeignKey(entity = Lexeme::class, parentColumns = ["id"], childColumns = ["base_id"])
    ],
    indices = [
        Index(value = ["external_id"], unique = true),
        Index(value = ["base_id"])
    ]
)
data class Lexeme(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val lexemeId: Long = 0,
    @ColumnInfo(name = "external_id") val externalId: String,
    @ColumnInfo(name = "form") val form: String,
    @ColumnInfo(name = "base_id") val baseId: Long?
)

data class LexemeWithRelations(
    @Embedded val lexeme: Lexeme,
    @Relation(parentColumn = "base_id", entityColumn = "id") val base: Lexeme?,
    @Relation(parentColumn = "id", entityColumn = "lexeme_id") val properties: List<Property>
)

@Dao
interface LexemeDao {
    @Query("SELECT * FROM lexeme")
    fun getAll(): Flow<List<Lexeme>>

    @Transaction
    @Query("SELECT * FROM lexeme")
    fun getAllWithRelations(): Flow<List<LexemeWithRelations>>

    @Query("SELECT COUNT(*) FROM lexeme")
    fun count(): Flow<Int>

    @Insert
    suspend fun insertAll(vararg lexemes: Lexeme)
}
