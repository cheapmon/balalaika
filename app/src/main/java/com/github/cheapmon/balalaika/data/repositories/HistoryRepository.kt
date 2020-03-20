package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.entities.HistoryEntry
import com.github.cheapmon.balalaika.data.entities.HistoryEntryDao
import kotlinx.coroutines.flow.map

class HistoryRepository private constructor(
    private val historyEntryDao: HistoryEntryDao
) {
    val historyEntries = historyEntryDao.getAllWithCategory().map { it.reversed() }

    suspend fun removeEntry(historyEntry: HistoryEntry) {
        historyEntryDao.removeAll(historyEntry)
    }

    suspend fun addEntry(historyEntry: HistoryEntry) {
        historyEntryDao.insertAll(historyEntry)
    }

    suspend fun clearHistory() {
        historyEntryDao.clear()
    }

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null

        fun getInstance(
            historyEntryDao: HistoryEntryDao
        ): HistoryRepository {
            return instance ?: synchronized(this) {
                instance ?: HistoryRepository(
                    historyEntryDao
                ).also { instance = it }
            }
        }
    }
}