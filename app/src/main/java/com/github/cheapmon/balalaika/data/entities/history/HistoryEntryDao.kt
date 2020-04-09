package com.github.cheapmon.balalaika.data.entities.history

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryEntryDao {
    @Query("SELECT * FROM search_history")
    fun getAll(): Flow<List<HistoryEntry>>

    @Transaction
    @Query("SELECT * FROM search_history")
    fun getAllWithCategory(): Flow<List<HistoryEntryWithCategory>>

    @Query("DELETE FROM search_history")
    suspend fun clear()

    @Query("SELECT COUNT(*) FROM search_history")
    fun count(): Flow<Int>

    @Insert
    suspend fun insertAll(vararg historyEntries: HistoryEntry)

    @Query("DELETE FROM search_history WHERE `query` = (:query)")
    suspend fun removeSimilar(query: String)

    @Delete
    suspend fun removeAll(vararg historyEntries: HistoryEntry)
}