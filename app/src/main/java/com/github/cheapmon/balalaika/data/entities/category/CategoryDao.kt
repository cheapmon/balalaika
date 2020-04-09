package com.github.cheapmon.balalaika.data.entities.category

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): Flow<List<Category>>

    @Query("SELECT COUNT(*) FROM category")
    fun count(): Flow<Int>

    @Insert
    suspend fun insertAll(vararg categories: Category)
}