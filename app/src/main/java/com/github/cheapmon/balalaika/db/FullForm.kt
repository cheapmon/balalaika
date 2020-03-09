package com.github.cheapmon.balalaika.db

import androidx.paging.DataSource
import androidx.room.*

@Entity(tableName = "full_form")
data class FullForm(
        @PrimaryKey val id: String,
        @ColumnInfo(name = "lexeme_id") val lexemeId: String,
        @ColumnInfo(name = "full_form") val fullForm: String
)

@Dao
interface FullFormDao {
    @Query("SELECT * FROM full_form ORDER BY full_form ASC")
    fun getAll(): DataSource.Factory<Int, FullForm>

    @Query("SELECT * FROM full_form WHERE lexeme_id = (:lexemeId)")
    fun getLexemes(lexemeId: String): List<FullForm>

    @Query("SELECT * FROM full_form WHERE id = (:fullFormId) LIMIT 1")
    fun getById(fullFormId: String): FullForm?

    @Query("SELECT DISTINCT * FROM full_form WHERE id IN (:forms)")
    fun getAllById(forms: List<String>): List<FullForm>

    @Query("""SELECT DISTINCT id FROM full_form WHERE full_form LIKE (:text)""")
    fun getAllLike(text: String): List<String>

    @Query("""SELECT DISTINCT full_form.* FROM lexeme_property
                    LEFT JOIN full_form ON lexeme_property.lexeme_id = full_form.lexeme_id
                    WHERE lexeme_property.category_id = (:category)
                    ORDER BY value ASC""")
    fun getAllOrderedBy(category: String): DataSource.Factory<Int, FullForm>

    @Query("SELECT COUNT(*) FROM full_form WHERE full_form < (:fullForm)")
    fun getPositionOf(fullForm: String): Int

    @Query("SELECT count(*) FROM full_form")
    fun count(): Int

    @Insert
    fun insertAll(vararg fullForms: FullForm)
}