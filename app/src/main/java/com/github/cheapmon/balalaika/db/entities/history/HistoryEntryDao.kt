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
package com.github.cheapmon.balalaika.db.entities.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.cheapmon.balalaika.db.entities.category.Category
import kotlinx.coroutines.flow.Flow

/** Database link for [history entries][HistoryEntry] */
@Dao
interface HistoryEntryDao {
    /**
     * Get all [history entries][HistoryEntry]
     *
     * This also retrieves the associated [category][Category] if the search has been restricted
     */
    @Transaction
    @Query(
        """SELECT search_history.* FROM search_history
                JOIN dictionary ON search_history.dictionary_id = dictionary.id
                WHERE dictionary.is_active = 1"""
    )
    fun getAllWithCategory(): Flow<List<HistoryEntryWithCategory>>

    /** Remove all [history entries][HistoryEntry] from the database */
    @Transaction
    @Query(
        """DELETE FROM search_history
                WHERE id IN (SELECT search_history.id FROM search_history
                JOIN dictionary ON search_history.dictionary_id = dictionary.id
                WHERE dictionary.is_active = 1)"""
    )
    suspend fun clear()

    /** Insert all [history entries][HistoryEntry] into the database */
    @Insert
    suspend fun insertAll(vararg historyEntries: HistoryEntry)

    /** Remove all [history entries][HistoryEntry] with a similar [query] from the database */
    @Transaction
    @Query(
        """DELETE FROM search_history
                WHERE `query` = (:query)
                AND id IN (SELECT search_history.id FROM search_history
                JOIN dictionary ON search_history.dictionary_id = dictionary.id
                WHERE dictionary.is_active = 1)"""
    )
    suspend fun removeSimilar(query: String)

    /** Remove a single [history entry][HistoryEntry] from the database */
    @Delete
    suspend fun remove(historyEntry: HistoryEntry)

    /** Remove all search history entries associated with a dictionary */
    @Query("""DELETE FROM search_history WHERE dictionary_id = (:dictionaryId)""")
    suspend fun removeInDictionary(dictionaryId: String)
}
