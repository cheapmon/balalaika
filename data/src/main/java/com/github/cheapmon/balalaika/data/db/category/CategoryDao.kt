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
package com.github.cheapmon.balalaika.data.db.category

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Database link for [data categories][CategoryEntity] */
@Dao
internal interface CategoryDao {
    /** Get all data categories */
    @Query("SELECT * FROM categories")
    suspend fun getAll(): List<CategoryEntity>

    /** Get all data categories that can be used to order dictionary entries */
    @Query("""SELECT * FROM categories WHERE dictionary_id = (:dictionaryId) AND sortable = 1""")
    fun getSortable(dictionaryId: String): Flow<List<CategoryEntity>>

    /** Insert all data categories into the database */
    @Insert
    suspend fun insertAll(categories: List<CategoryEntity>)

    /** Remove all data categories belonging to a dictionary */
    @Query("""DELETE FROM categories WHERE dictionary_id = (:dictionaryId)""")
    suspend fun removeInDictionary(dictionaryId: String)

    /** Find a data category by its id */
    @Query("""SELECT * FROM categories WHERE dictionary_id = (:dictionaryId) AND id = (:id) LIMIT 1""")
    suspend fun findById(dictionaryId: String, id: String): CategoryEntity?

    /** Total number of categories */
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}
