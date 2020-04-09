package com.github.cheapmon.balalaika.data.entities.category

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category WHERE order_by = 1")
    fun getSortable(): Flow<List<Category>>

    @Insert
    suspend fun insertAll(vararg categories: Category)
}