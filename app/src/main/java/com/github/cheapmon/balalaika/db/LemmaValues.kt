package com.github.cheapmon.balalaika.db

import androidx.room.*

@Entity(foreignKeys = [
    ForeignKey(entity = Lemma::class, parentColumns = ["id"], childColumns = ["lemma_id"]),
    ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["category_id"])
], primaryKeys = ["lemma_id", "category_id"], indices = [Index(value = ["category_id"])],
        tableName = "lemma_value")
data class LemmaValue(
        @ColumnInfo(name = "lemma_id") val lemmaId: String,
        @ColumnInfo(name = "category_id") val categoryId: String,
        @ColumnInfo(name = "value") val value: String?
)

@Dao
interface LemmaValueDao {
    @Query("SELECT * FROM lemma_value")
    fun getAll(): List<LemmaValue>

    @Query("SELECT * FROM lemma_value WHERE lemma_id = (:word)")
    fun getValues(word: String): List<LemmaValue>

    @Query("SELECT count(*) FROM lemma_value")
    fun count(): Int

    @Insert
    fun insertAll(vararg lemmaValues: LemmaValue)

    @Transaction
    @Query("""SELECT lemma_value.lemma_id, lemma_value.category_id, lemma_value.value
                    FROM lemma_value LEFT JOIN lexeme ON lemma_value.lemma_id = lexeme.lemma_id
                    WHERE lexeme.lexeme = (:lexeme)""")
    fun findByLexeme(lexeme: String): List<LemmaValue>
}