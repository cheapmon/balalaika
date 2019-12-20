package com.github.cheapmon.balalaika.db

import androidx.room.*

@Entity(foreignKeys = [
    ForeignKey(entity = Lemma::class, parentColumns = ["id"], childColumns = ["lemma_id"])
], primaryKeys = ["lemma_id", "lexeme"], indices = [Index(value = ["lemma_id"])])
data class Lexeme(
        @ColumnInfo(name = "lemma_id") val lemmaId: String,
        @ColumnInfo(name = "lexeme") val lexeme: String
)

@Dao
interface LexemeDao {
    @Query("SELECT * FROM lexeme")
    fun getAll(): List<Lexeme>

    @Query("SELECT * FROM lexeme WHERE lemma_id = (:lemmaId)")
    fun getLexemes(lemmaId: String): List<Lexeme>

    @Query("SELECT count(*) FROM lexeme")
    fun count(): Int

    @Insert
    fun insertAll(vararg lexemes: Lexeme)
}