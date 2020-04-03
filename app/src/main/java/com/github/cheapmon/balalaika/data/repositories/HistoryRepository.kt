package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.entities.HistoryEntry
import com.github.cheapmon.balalaika.data.entities.HistoryEntryDao
import com.github.cheapmon.balalaika.data.entities.HistoryEntryWithRestriction
import com.github.cheapmon.balalaika.data.entities.SearchRestriction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyEntryDao: HistoryEntryDao
) {
    val historyEntries: Flow<List<HistoryEntryWithRestriction>> =
        historyEntryDao.getAllWithCategory().map {
            it.reversed().map { entry ->
                if (entry.category != null && entry.historyEntry.restriction != null) {
                    HistoryEntryWithRestriction(
                        entry.historyEntry,
                        SearchRestriction.Some(entry.category, entry.historyEntry.restriction)
                    )
                } else {
                    HistoryEntryWithRestriction(entry.historyEntry, SearchRestriction.None)
                }
            }
        }

    suspend fun removeEntry(historyEntry: HistoryEntry) {
        historyEntryDao.removeAll(historyEntry)
    }

    suspend fun addEntry(historyEntry: HistoryEntry) {
        historyEntryDao.insertAll(historyEntry)
    }

    suspend fun removeSimilarEntries(historyEntry: HistoryEntry) {
        historyEntryDao.removeSimilar(historyEntry.query)
    }

    suspend fun clearHistory() {
        historyEntryDao.clear()
    }
}