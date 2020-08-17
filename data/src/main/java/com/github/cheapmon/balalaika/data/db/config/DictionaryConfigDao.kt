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
package com.github.cheapmon.balalaika.data.db.config

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Database link for [dictionary configurations][DictionaryConfig] */
@Dao
internal interface DictionaryConfigDao {
    /** Get the configuration for a dictionary */
    @Query("""SELECT * FROM config WHERE id = (:dictionaryId) LIMIT 1""")
    fun getConfigFor(dictionaryId: String): Flow<DictionaryConfig?>

    /** Insert a configuration into the database */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: DictionaryConfig)

    /** Update the configuration for a dictionary */
    @Update
    suspend fun update(config: DictionaryConfig)

    /** Remove the configuration for a dictionary */
    @Query("""DELETE FROM config WHERE id = (:dictionaryId)""")
    suspend fun removeConfigFor(dictionaryId: String)
}
