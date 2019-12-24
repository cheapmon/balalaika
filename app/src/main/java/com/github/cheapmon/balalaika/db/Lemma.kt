package com.github.cheapmon.balalaika.db

import androidx.room.*

@Entity
data class Lemma(
        @PrimaryKey val id: String
)

@Dao
interface LemmaDao {
    @Query("SELECT * FROM lemma")
    fun getAll(): List<Lemma>

    @Query("SELECT * FROM lemma WHERE id = (:id) LIMIT 1")
    fun findById(id: String): Lemma?

    @Query("SELECT count(*) FROM lemma")
    fun count(): Int

    @Insert
    fun insertAll(vararg lemmata: Lemma)

    @Transaction
    @Query("SELECT * FROM lemma")
    fun getAllWithLexemes(): List<LemmaWithLexemes>

    @Transaction
    @Query("SELECT * FROM lemma WHERE id = (:id) LIMIT 1")
    fun findByIdWithLexemes(id: String): LemmaWithLexemes?
}