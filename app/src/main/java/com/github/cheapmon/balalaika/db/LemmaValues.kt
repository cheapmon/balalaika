package com.github.cheapmon.balalaika.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(foreignKeys = [
    ForeignKey(entity = Lemma::class, parentColumns = ["id"], childColumns = ["lemma_id"]),
    ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["category_id"])
], primaryKeys = ["lemma_id", "category_id"], indices = [Index(value = ["category_id"])],
        tableName = "lemma_property")
data class LemmaProperty(
        @ColumnInfo(name = "lemma_id") val lemmaId: String,
        @ColumnInfo(name = "category_id") val categoryId: String,
        @ColumnInfo(name = "value") val value: String?
)

@Dao
interface LemmaPropertyDao {
    @Query("SELECT * FROM lemma_property")
    fun getAll(): List<LemmaProperty>

    @Query("SELECT * FROM lemma_property WHERE lemma_id = (:word)")
    fun getValues(word: String): List<LemmaProperty>

    @Query("SELECT count(*) FROM lemma_property")
    fun count(): Int

    @Insert
    fun insertAll(vararg lemmaProperties: LemmaProperty)

    @Transaction
    @Query("""SELECT lemma_property.lemma_id, lemma_property.category_id, lemma_property.value
                    FROM lemma_property LEFT JOIN lexeme ON lemma_property.lemma_id = lexeme.lemma_id
                    WHERE lexeme.lexeme = (:lexeme)""")
    fun findByLexeme(lexeme: String): List<LemmaProperty>
}