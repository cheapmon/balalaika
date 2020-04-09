package com.github.cheapmon.balalaika.data.entities

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
    @ColumnInfo(name = "base_id") val baseId: Long?,
    @ColumnInfo(name = "is_bookmark") val isBookmark: Boolean = false
)

data class LexemeWithRelations(
    @Embedded val lexeme: Lexeme,
    @Relation(parentColumn = "base_id", entityColumn = "id") val base: Lexeme?,
    @Relation(parentColumn = "id", entityColumn = "lexeme_id") val properties: List<Property>
)

data class _DictionaryEntry(
    val lexeme: Lexeme,
    val base: Lexeme?,
    val properties: List<PropertyWithRelations>
)

@Dao
interface LexemeDao {
    @Query("SELECT * FROM lexeme")
    fun getAll(): Flow<List<Lexeme>>

    @Transaction
    @Query("SELECT * FROM lexeme")
    fun getAllWithRelations(): Flow<List<LexemeWithRelations>>

    @Query("SELECT * FROM lexeme WHERE is_bookmark = 1")
    fun getBookmarks(): Flow<List<Lexeme>>

    @Query("SELECT * FROM lexeme WHERE id = (:id) LIMIT 1")
    fun findById(id: Long): Flow<Lexeme?>

    @Query("SELECT * FROM lexeme WHERE form LIKE '%' || (:query) || '%'")
    fun findByForm(query: String): Flow<List<Lexeme>>

    @Query("SELECT COUNT(*) FROM lexeme")
    fun count(): Flow<Int>

    @Query("UPDATE lexeme SET is_bookmark = NOT is_bookmark WHERE id = (:id)")
    suspend fun toggleBookmark(id: Long)

    @Query("UPDATE lexeme SET is_bookmark = 0 WHERE is_bookmark = 1")
    suspend fun clearBookmarks()

    @Insert
    suspend fun insertAll(vararg lexemes: Lexeme)
}
