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
package com.github.cheapmon.balalaika.db.entities.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

/** Database link for cache entries */
@Dao
interface CacheEntryDao {
    /** Get some entries from the main cache */
    @Query(
        """SELECT lexeme_id FROM cache_entry
                WHERE id >= (:startId) AND id < (:startId) + (:count)"""
    )
    suspend fun getFromDictionaryCache(startId: Long, count: Int): List<String>

    /** Find the position of an entry in the main cache */
    @Transaction
    @Query(
        """SELECT id FROM cache_entry
                WHERE lexeme_id = (:lexemeId)
                LIMIT 1"""
    )
    suspend fun findEntryInDictionaryCache(lexemeId: String): Long?

    /** Insert entries into main cache */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntoDictionaryCache(entries: List<CacheEntry>)

    /** Remove all main cache entries */
    @Query("DELETE FROM cache_entry")
    suspend fun clearDictionaryCache()
}
