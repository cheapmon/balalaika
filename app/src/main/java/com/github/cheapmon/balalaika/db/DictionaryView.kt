package com.github.cheapmon.balalaika.db

import androidx.room.*

@Entity(primaryKeys = ["view_id", "category_id"], foreignKeys = [
    ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["category_id"])
], indices = [Index(value = ["category_id"])], tableName = "dictionary_view")
data class DictionaryView(
        @ColumnInfo(name = "view_id") val viewId: String,
        @ColumnInfo(name = "category_id") val categoryId: String
)

@Dao
interface DictionaryViewDao {
    @Query("SELECT * FROM dictionary_view")
    fun getAll(): List<DictionaryView>

    @Query("SELECT category_id FROM dictionary_view WHERE view_id = (:viewId)")
    fun findCategoryIdsForView(viewId: String): List<String>

    @Query("SELECT count(*) FROM dictionary_view")
    fun count(): Int

    @Insert
    fun insertAll(vararg dictionaryViews: DictionaryView)
}