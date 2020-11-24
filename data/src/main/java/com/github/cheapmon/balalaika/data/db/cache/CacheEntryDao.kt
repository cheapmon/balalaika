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
package com.github.cheapmon.balalaika.data.db.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/** Database link for cache entries */
@Dao
internal interface CacheEntryDao {
    /** Get all cache entries */
    @Query("SELECT * FROM cache_entries")
    suspend fun getAll(): List<CacheEntry>

    /** Get some entries from the main cache */
    @Query("""SELECT lexeme_id FROM cache_entries LIMIT (:count) OFFSET (:offset)""")
    suspend fun getPage(count: Int, offset: Long): List<String>

    /** Find the position of an entry in the main cache */
    @Query("""SELECT id FROM cache_entries WHERE lexeme_id = (:lexemeId) LIMIT 1""")
    suspend fun findEntry(lexemeId: String): Long?

    /** Insert entries into main cache */
    @Insert
    suspend fun insertAll(entries: List<CacheEntry>)

    /** Remove all main cache entries */
    @Query("DELETE FROM cache_entries")
    suspend fun clear()

    @Query("SELECT COUNT(*) FROM cache_entries")
    suspend fun count(): Int
}
