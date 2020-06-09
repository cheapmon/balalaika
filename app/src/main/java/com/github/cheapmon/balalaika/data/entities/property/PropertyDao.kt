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
package com.github.cheapmon.balalaika.data.entities.property

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import kotlinx.coroutines.flow.Flow

/** Database link for [properties][Property] */
@Dao
interface PropertyDao {
    /** Get all [properties][PropertyWithRelations] filtered by certain [categories][Category] */
    @Transaction
    @Query(
        """SELECT property.id, property.category_id, property.lexeme_id, property.value 
                    FROM property JOIN category ON property.category_id = category.id
                    JOIN lexeme ON property.lexeme_id = lexeme.id
                    WHERE category.id IN (:categoryIds) AND category.hidden = 0"""
    )
    fun getAllFiltered(categoryIds: List<Long>): Flow<List<PropertyWithRelations>>

    /** Get all [properties][PropertyWithRelations] with visible [categories][Category] */
    @Transaction
    @Query(
        """SELECT property.id, property.category_id, property.lexeme_id, property.value
                    FROM property JOIN category ON property.category_id = category.id
                    JOIN lexeme ON property.lexeme_id = lexeme.id
                    WHERE category.hidden = 0"""
    )
    fun getAllVisible(): Flow<List<PropertyWithRelations>>

    /** Find [properties][PropertyWithRelations] by [lexeme][Lexeme] */
    @Transaction
    @Query("""SELECT * FROM property WHERE property.lexeme_id IN (:lexemeId)""")
    fun findByLexemeId(lexemeId: List<Long>): Flow<List<PropertyWithRelations>>

    /** Find [properties][PropertyWithRelations] with a value similar to [query] */
    @Transaction
    @Query("SELECT * FROM property WHERE value LIKE '%' || (:query) || '%'")
    fun findByValue(query: String): Flow<List<PropertyWithRelations>>

    /**
     * Find [properties][PropertyWithRelations] with a value similar to [query], also fulfilling
     * a certain restriction
     *
     * @see SearchRestriction
     */
    @Transaction
    @Query(
        """SELECT property.id, property.category_id, property.lexeme_id, property.value
                    FROM property JOIN lexeme ON property.lexeme_id = lexeme.id
                    WHERE lexeme_id IN (SELECT DISTINCT lexeme_id FROM property 
                    WHERE category_id = (:categoryId) AND value LIKE '%' || (:restriction) || '%')
                    AND (value LIKE '%' || (:query) || '%' OR form LIKE '%' || (:query) || '%')"""
    )
    fun findByValueRestricted(
        query: String,
        categoryId: Long,
        restriction: String
    ): Flow<List<PropertyWithRelations>>

    /** Number of [properties][Property] in database */
    @Query("SELECT COUNT(*) FROM property")
    fun count(): Flow<Int>

    /** Insert all [properties][Property] into the database */
    @Insert
    suspend fun insertAll(vararg properties: Property)
}