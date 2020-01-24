package com.github.cheapmon.balalaika.db

import androidx.room.*

@Entity(foreignKeys = [
    ForeignKey(entity = Lexeme::class, parentColumns = ["id"], childColumns = ["lexeme_id"]),
    ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["category_id"])
], indices = [Index(value = ["lexeme_id"]), Index(value = ["category_id"])], tableName = "lexeme_property")
data class LexemeProperty(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(name = "lexeme_id") val lexemeId: String,
        @ColumnInfo(name = "category_id") val categoryId: String,
        @ColumnInfo(name = "value") val value: String?
)

@Dao
interface LexemePropertyDao {
    @Query("SELECT * FROM lexeme_property")
    fun getAll(): List<LexemeProperty>

    @Query("SELECT * FROM lexeme_property WHERE lexeme_id = (:word)")
    fun getValues(word: String): List<LexemeProperty>

    @Query("SELECT count(*) FROM lexeme_property")
    fun count(): Int

    @Insert
    fun insertAll(vararg lexemeProperties: LexemeProperty)

    @Transaction
    @Query("""SELECT lexeme_property.id, lexeme_property.lexeme_id, lexeme_property.category_id, lexeme_property.value
                    FROM lexeme_property LEFT JOIN full_form ON lexeme_property.lexeme_id = full_form.lexeme_id
                    LEFT JOIN category ON category.id = lexeme_property.category_id
                    WHERE full_form.id = (:fullFormId) AND category.hidden = 0""")
    fun findByFullForm(fullFormId: String): List<LexemeProperty>
}