package com.github.cheapmon.balalaika.data.entities.view

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryViewDao {
    @Query("SELECT * FROM dictionary_view")
    fun getAll(): Flow<List<DictionaryView>>

    @Transaction
    @Query("SELECT * FROM dictionary_view")
    fun getAllWithCategories(): Flow<List<DictionaryViewWithCategories>>

    @Query(
        """SELECT category_id FROM dictionary_view_to_category 
                    WHERE dictionary_view_id = (:id)"""
    )
    fun findCategoriesById(id: Long): Flow<List<Long>>

    @Query("SELECT COUNT(*) FROM dictionary_view")
    fun count(): Flow<Int>

    @Insert
    suspend fun insertAll(vararg dictionaryViews: DictionaryView)

    @Insert
    suspend fun insertAll(vararg dictionaryViewToCategory: DictionaryViewToCategory)
}