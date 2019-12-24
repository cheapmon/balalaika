package com.github.cheapmon.balalaika.db

import androidx.room.*

@Entity(primaryKeys = ["lemma_id", "lexeme"], indices = [Index(value = ["lemma_id"])])
data class Lexeme(
        @ColumnInfo(name = "lemma_id") val lemmaId: String,
        @ColumnInfo(name = "lexeme") val lexeme: String
)

data class LemmaWithLexemes(
        @Embedded val lemma: Lemma,
        @Relation(
                parentColumn = "id",
                entityColumn = "lemma_id"
        )
        val lexemes: List<Lexeme>
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