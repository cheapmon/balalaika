package com.github.cheapmon.balalaika.data.entities.history

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryEntryDao {
    @Transaction
    @Query("SELECT * FROM search_history")
    fun getAllWithCategory(): Flow<List<HistoryEntryWithCategory>>

    @Query("DELETE FROM search_history")
    suspend fun clear()

    @Insert
    suspend fun insertAll(vararg historyEntries: HistoryEntry)

    @Query("DELETE FROM search_history WHERE `query` = (:query)")
    suspend fun removeSimilar(query: String)

    @Delete
    suspend fun remove(historyEntry: HistoryEntry)
}