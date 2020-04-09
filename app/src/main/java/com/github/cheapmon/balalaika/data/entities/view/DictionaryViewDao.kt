package com.github.cheapmon.balalaika.data.entities.view

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryViewDao {
    @Transaction
    @Query("SELECT * FROM dictionary_view")
    fun getAllWithCategories(): Flow<List<DictionaryViewWithCategories>>

    @Query(
        """SELECT category_id FROM dictionary_view_to_category 
                    WHERE dictionary_view_id = (:id)"""
    )
    fun findCategoriesById(id: Long): Flow<List<Long>>

    @Insert
    suspend fun insertAll(vararg dictionaryViews: DictionaryView)

    @Insert
    suspend fun insertAll(vararg dictionaryViewToCategory: DictionaryViewToCategory)
}