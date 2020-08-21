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
package com.github.cheapmon.balalaika.data.db.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import kotlinx.coroutines.flow.Flow

/** Database link for [history items][HistoryItemEntity] */
@Dao
internal interface HistoryItemDao {
    /**
     * Get all [history items][HistoryItemEntity]
     *
     * This also retrieves the associated [category][CategoryEntity] if the search has been
     * restricted
     */
    @Transaction
    @Query(
        """SELECT search_history.* FROM search_history
                WHERE dictionary_id = (:dictionaryId)"""
    )
    fun getAll(dictionaryId: String): Flow<List<HistoryItemWithCategory>>

    /** Insert a [history item][HistoryItemEntity] into the database */
    @Insert
    suspend fun insert(historyItem: HistoryItemEntity)

    /** Remove all [history items][HistoryItemEntity] with a similar [query] from the database */
    @Query(
        """DELETE FROM search_history
                WHERE `query` = (:query)
                AND id IN (SELECT search_history.id FROM search_history
                WHERE dictionary_id = (:dictionaryId))"""
    )
    suspend fun removeSimilar(dictionaryId: String, query: String)

    /** Remove a single [history item][HistoryItemEntity] from the database */
    @Delete
    suspend fun remove(historyEntry: HistoryItemEntity)

    /** Remove all search history items associated with a dictionary */
    @Query("""DELETE FROM search_history WHERE dictionary_id = (:dictionaryId)""")
    suspend fun removeInDictionary(dictionaryId: String)
}
