package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.entities.HistoryEntry
import com.github.cheapmon.balalaika.data.entities.HistoryEntryDao
import kotlinx.coroutines.flow.map

class HistoryRepository(
    private val historyEntryDao: HistoryEntryDao
) {
    val historyEntries = historyEntryDao.getAllWithCategory().map { it.reversed() }

    suspend fun removeEntry(historyEntry: HistoryEntry) {
        historyEntryDao.removeAll(historyEntry)
    }

    suspend fun clearHistory() {
        historyEntryDao.clear()
    }
}