package com.github.cheapmon.balalaika.db

import androidx.paging.DataSource
import androidx.room.*

@Entity(tableName = "full_form")
data class FullForm(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "lexeme_id") val lexemeId: String,
        @ColumnInfo(name = "full_form") val fullForm: String
)

@Dao
interface FullFormDao {
    @Query("SELECT * FROM full_form ORDER BY full_form ASC")
    fun getAll(): DataSource.Factory<Int, FullForm>

    @Query("SELECT * FROM full_form WHERE lexeme_id = (:lexemeId)")
    fun getLexemes(lexemeId: String): List<FullForm>

    @Query("SELECT count(*) FROM full_form")
    fun count(): Int

    @Insert
    fun insertAll(vararg fullForms: FullForm)
}