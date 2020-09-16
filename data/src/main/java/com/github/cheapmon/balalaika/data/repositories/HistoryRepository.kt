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

import com.github.cheapmon.balalaika.data.db.history.HistoryItemDao
import com.github.cheapmon.balalaika.data.mappers.HistoryItemToHistoryItemEntity
import com.github.cheapmon.balalaika.data.mappers.HistoryItemWithCategoryToHistoryItem
import com.github.cheapmon.balalaika.model.HistoryItem
import com.github.cheapmon.balalaika.model.InstalledDictionary
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Suppress("EXPERIMENTAL_API_USAGE")
@Singleton
public class HistoryRepository @Inject internal constructor(
    private val dao: HistoryItemDao,
    private val toHistoryItem: HistoryItemWithCategoryToHistoryItem,
    private val fromHistoryItem: HistoryItemToHistoryItemEntity
) {
    public fun getHistoryItems(dictionary: InstalledDictionary): Flow<List<HistoryItem>> =
        dao.getAll(dictionary.id)
            .map { list -> list.map { entity -> toHistoryItem(entity) } }

    public suspend fun addToHistory(dictionary: InstalledDictionary, historyItem: HistoryItem) {
        val entity = fromHistoryItem(historyItem, dictionary)
        dao.removeSimilar(dictionary.id, historyItem.query)
        dao.insert(entity)
    }

    public suspend fun removeFromHistory(
        dictionary: InstalledDictionary,
        historyItem: HistoryItem
    ) {
        dao.remove(fromHistoryItem(historyItem, dictionary))
    }

    public suspend fun clearHistory(dictionary: InstalledDictionary): Unit =
        dao.removeInDictionary(dictionary.id)
}
