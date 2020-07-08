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
package com.github.cheapmon.balalaika.db.entities.property

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.cheapmon.balalaika.db.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.db.entities.view.DictionaryView

/** Database link for [properties][Property] */
@Dao
interface PropertyDao {
    /**
     * Get all properties of a [lexeme][Lexeme], depending on a
     * [dictionary view][DictionaryView]
     */
    @Transaction
    @Query(
        """SELECT * FROM property
                JOIN category ON property.category_id = category.id
                WHERE lexeme_id = (:lexemeId)
                AND category_id IN (SELECT category_id FROM dictionary_view_to_category
                WHERE dictionary_view_id = (:dictionaryViewId))
                AND hidden = 0"""
    )
    suspend fun getProperties(lexemeId: String, dictionaryViewId: Long): List<PropertyWithCategory>

    /** Insert all [properties][Property] into the database */
    @Insert
    suspend fun insertAll(properties: List<Property>)
}
