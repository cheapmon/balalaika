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
package com.github.cheapmon.balalaika.data.db.dictionary

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Database for [dictionaries][DictionaryEntity] */
@Dao
internal interface DictionaryDao {
    /** Get all available dictionaries */
    @Query("SELECT * FROM dictionaries")
    fun getAll(): Flow<List<DictionaryEntity>>

    /** Find a dictionary by its id */
    @Query("SELECT * FROM dictionaries WHERE id = (:id) LIMIT 1")
    fun findById(id: String): Flow<DictionaryEntity?>

    /** Insert dictionaries into the database */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dictionary: List<DictionaryEntity>)

    /** Remove a dictionary from the database */
    @Query("DELETE FROM dictionaries WHERE id = (:id)")
    suspend fun remove(id: String)

    /** Delete all dictionaries from the database */
    @Query("DELETE FROM dictionaries")
    suspend fun clear()
}
