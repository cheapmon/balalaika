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
package com.github.cheapmon.balalaika.data.db.view

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import kotlinx.coroutines.flow.Flow

/** Database link for [dictionary views][DictionaryViewEntity] */
@Dao
internal interface DictionaryViewDao {
    /**
     * Get all [dictionary views][DictionaryViewEntity] and their associated [data categories][CategoryEntity]
     */
    @Transaction
    @Query(
        """SELECT dictionary_views.*
                FROM dictionary_views
                WHERE dictionary_id = (:dictionaryId)"""
    )
    fun getAll(dictionaryId: String): Flow<List<DictionaryViewWithCategories>>

    /** Insert all [dictionary views][DictionaryViewEntity] into the database */
    @Insert
    suspend fun insertViews(dictionaryViews: List<DictionaryViewEntity>)

    /** Insert all [dictionary view relations][DictionaryViewToCategory] into the database */
    @Insert
    suspend fun insertRelation(dictionaryViewToCategory: List<DictionaryViewToCategory>)

    /** Remove all dictionary views associated with a dictionary */
    @Query("""DELETE FROM dictionary_views WHERE dictionary_id = (:dictionaryId)""")
    suspend fun removeViews(dictionaryId: String)

    /** Remove all dictionary view relations associated with a dictionary */
    @Query("""DELETE FROM dictionary_view_to_category WHERE dictionary_id = (:dictionaryId)""")
    suspend fun removeRelations(dictionaryId: String)

    /** Find a dictionary view by its id */
    @Query("""SELECT * FROM dictionary_views WHERE id = (:id) LIMIT 1""")
    suspend fun findById(id: String): DictionaryViewEntity?
}
