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

@Dao
interface DictionaryDao {
    @Query("SELECT * FROM dictionary")
    fun getAll(): Flow<List<Dictionary>>

    @Query("SELECT * FROM dictionary WHERE id = (:id) LIMIT 1")
    fun getById(id: Long): Flow<Dictionary?>

    @Query("SELECT * FROM dictionary WHERE is_active LIMIT 1")
    suspend fun getActive(): Dictionary?

    @Query("UPDATE dictionary SET is_active = 0")
    suspend fun deactivateAll()

    @Query("""UPDATE dictionary SET is_active = NOT is_active
                    WHERE external_id = (:externalId)""")
    suspend fun toggleIsActive(externalId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dictionary: List<Dictionary>)

    @Query("DELETE FROM dictionary")
    suspend fun clear()
}
