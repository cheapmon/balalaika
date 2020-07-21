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
package com.github.cheapmon.balalaika.db.entities.dictionary

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Database for [dictionaries][Dictionary] */
@Dao
interface DictionaryDao {
    /** Get all available dictionaries */
    @Query("SELECT * FROM dictionary")
    fun getAll(): Flow<List<Dictionary>>

    /** Find a dictionary by its id */
    @Query("SELECT * FROM dictionary WHERE id = (:id) LIMIT 1")
    fun findById(id: String): Flow<Dictionary?>

    /** Find the currently active dictionary */
    @Query("SELECT * FROM dictionary WHERE is_active LIMIT 1")
    fun getActive(): Flow<Dictionary?>

    /** Set a dictionary to be active */
    @Query("""UPDATE dictionary SET is_active = (id == (:id))""")
    suspend fun setActive(id: String)

    /** Set a dictionary to be inactive */
    @Query("""UPDATE dictionary SET is_active = 0 WHERE id = (:id)""")
    suspend fun setInactive(id: String)

    /** Set a dictionary to be installed */
    @Query("""UPDATE dictionary SET is_installed = 1 WHERE id = (:id)""")
    suspend fun setInstalled(id: String)

    /** Indicate a dictionary to not be installed */
    @Query("""UPDATE dictionary SET is_installed = 0 WHERE id = (:id)""")
    suspend fun setUninstalled(id: String)

    /** Set a dictionary to be updatable */
    @Query("""UPDATE dictionary SET is_updatable = 1 WHERE id = (:id)""")
    suspend fun setUpdatable(id: String)

    /** Indicate a dictionary to no longer be updatable */
    @Query("""UPDATE dictionary SET is_updatable = 0 WHERE id = (:id)""")
    suspend fun setUnupdatable(id: String)

    /** Insert dictionaries into the database */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dictionary: List<Dictionary>)

    /** Remove a dictionary from the database */
    @Query("DELETE FROM dictionary WHERE id = (:id)")
    suspend fun remove(id: String)

    /** Delete all dictionaries from the database */
    @Query("DELETE FROM dictionary")
    suspend fun clear()
}
