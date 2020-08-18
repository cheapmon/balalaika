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
package com.github.cheapmon.balalaika.data.db.property

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity

/** Database link for [properties][PropertyEntity] */
@Dao
internal interface PropertyDao {
    /**
     * Get all properties of a [lexeme][LexemeEntity], depending on a
     * [dictionary view][DictionaryViewEntity]
     */
    @Transaction
    @Query(
        """SELECT properties.* FROM properties
                JOIN categories ON properties.category_id = categories.id
                WHERE lexeme_id = (:lexemeId)
                AND category_id IN (SELECT category_id FROM dictionary_view_to_category
                WHERE dictionary_view_id = (:dictionaryViewId))
                AND hidden = 0"""
    )
    suspend fun getProperties(
        lexemeId: String,
        dictionaryViewId: String
    ): List<PropertyWithCategory>

    /** Insert all [properties][PropertyEntity] into the database */
    @Insert
    suspend fun insertAll(properties: List<PropertyEntity>)

    /** Remove all properties associated with a dictionary */
    @Query("""DELETE FROM properties WHERE dictionary_id = (:dictionaryId)""")
    suspend fun removeInDictionary(dictionaryId: String)
}
