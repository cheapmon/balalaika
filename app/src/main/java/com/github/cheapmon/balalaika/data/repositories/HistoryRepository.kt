package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.entities.HistoryEntry
import com.github.cheapmon.balalaika.data.entities.HistoryEntryDao
import kotlinx.coroutines.flow.map

class HistoryRepository(
    private val historyEntryDao: HistoryEntryDao
) {
    val historyEntries = historyEntryDao.getAllWithCategory().map { it.reversed() }

    suspend fun addEntry(query: String, categoryId: Long, restriction: String) {
        historyEntryDao.insertAll(HistoryEntry(0, categoryId, restriction, query))
    }

    suspend fun clearHistory() {
        historyEntryDao.clear()
    }
}