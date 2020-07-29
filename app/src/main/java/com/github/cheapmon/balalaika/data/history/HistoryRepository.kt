/*
 * Copyright 2020 Simon Kaleschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cheapmon.balalaika.data.history

import com.github.cheapmon.balalaika.db.entities.history.HistoryEntry
import com.github.cheapmon.balalaika.db.entities.history.HistoryEntryDao
import com.github.cheapmon.balalaika.db.entities.history.HistoryEntryWithRestriction
import com.github.cheapmon.balalaika.ui.history.HistoryFragment
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Search history data handling
 *
 * @see HistoryFragment
 */
@ActivityScoped
class HistoryRepository @Inject constructor(
    private val historyEntryDao: HistoryEntryDao,
    private val mapper: HistoryEntryMapper
) {
    /** All [search history entries][HistoryEntry] */
    val historyEntries: Flow<List<HistoryEntryWithRestriction>> =
        historyEntryDao.getAllWithCategory().map {
            it.reversed().map { entry -> mapper.map(entry) }
        }

    /** Remove [search history entry][HistoryEntry] */
    suspend fun removeEntry(historyEntry: HistoryEntry) {
        historyEntryDao.remove(historyEntry)
    }

    /** Add [search history entry][HistoryEntry] */
    suspend fun addEntry(historyEntry: HistoryEntry) {
        historyEntryDao.insertAll(historyEntry)
    }

    /** Remove all [search history entries][HistoryEntry] with similar queries */
    suspend fun removeSimilarEntries(historyEntry: HistoryEntry) {
        historyEntryDao.removeSimilar(historyEntry.query)
    }

    /** Remove all [search history entries][HistoryEntry] */
    suspend fun clearHistory() {
        historyEntryDao.clear()
    }
}
