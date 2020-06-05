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
package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.entities.history.HistoryEntry
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntryDao
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntryWithRestriction
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.di.ActivityScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ActivityScope
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
                    HistoryEntryWithRestriction(
                        entry.historyEntry,
                        SearchRestriction.None
                    )
                }
            }
        }

    suspend fun removeEntry(historyEntry: HistoryEntry) {
        historyEntryDao.remove(historyEntry)
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