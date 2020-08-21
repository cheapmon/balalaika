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
package com.github.cheapmon.balalaika.data.db.entry

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.data.db.property.PropertyDao
import com.github.cheapmon.balalaika.data.db.property.PropertyEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity

/**
 * Database link for [dictionary entries][DictionaryEntryEntity]
 *
 * Dictionary entry retrieval consists of two steps:
 * 1. Querying of matching [lexemes][LexemeEntity]
 * 2. Fetching associated dictionary entries on demand
 *
 * In this data access object, we handle the first step by fetching
 * [lexeme identifiers][LexemeEntity.id] based on the user configuration.
 *
 * @see PropertyDao.getProperties
 */
@Dao
internal interface DictionaryEntryDao {
    /**
     * Get all [lexeme ids][LexemeEntity.id], depending on a certain
     * [dictionary view][DictionaryViewEntity]
     *
     * This effectively checks for all [categories][CategoryEntity] that are displayed within the
     * dictionary view and selects only those lexemes that match one of those categories and aren't
     * hidden.
     *
     * _Note_: These lexemes are sorted only by the [form][LexemeEntity.form] and
     * [sequence][CategoryEntity.sequence] fields.
     */
    @Transaction
    @Query(
        """SELECT id FROM dictionary_entries
                    WHERE c_id IN (SELECT category_id FROM dictionary_view_to_category
                    WHERE dictionary_view_id = (:dictionaryViewId))
                    AND c_hidden = 0
                    AND dictionary_id = (:dictionaryId)
                    GROUP BY id
                    ORDER BY form ASC, c_sequence ASC"""
    )
    suspend fun getLexemes(dictionaryId: String, dictionaryViewId: String): List<String>

    /**
     * Get all [lexemes][LexemeEntity], depending on a certain [dictionary view][DictionaryViewEntity] and
     * sorted by a certain [data category][CategoryEntity]
     *
     * This effectively checks for all [categories][CategoryEntity] that are displayed within the
     * dictionary view and selects only those lexemes that match one of those categories and aren't
     * hidden, then sorts the remaining lexemes based on the given data category.
     */
    @Transaction
    @Query(
        """SELECT id
                    FROM dictionary_entries
                    JOIN (SELECT id AS o_id, p_value as o_value FROM dictionary_entries
                    WHERE c_id = (:categoryId) ORDER BY p_value ASC) AS ids ON o_id = id
                    WHERE c_id IN (SELECT category_id FROM dictionary_view_to_category
                    WHERE dictionary_view_id = (:dictionaryViewId))
                    AND c_hidden = 0
                    AND dictionary_id = (:dictionaryId)
                    GROUP BY id
                    ORDER BY o_value ASC, form ASC, c_sequence ASC"""
    )
    suspend fun getLexemes(
        dictionaryId: String,
        dictionaryViewId: String,
        categoryId: String
    ): List<String>

    /**
     * Find all [lexemes][LexemeEntity] whose [form][LexemeEntity.form] or
     * [property value][PropertyEntity.value] includes the given [query] string
     */
    @Transaction
    @Query(
        """SELECT id FROM dictionary_entries
                    WHERE (form LIKE '%' || (:query) || '%'
                    OR p_value LIKE '%' || (:query) || '%')
                    AND c_hidden = 0
                    AND dictionary_id = (:dictionaryId)
                    GROUP BY id
                    ORDER BY form ASC"""
    )
    suspend fun findLexemes(dictionaryId: String, query: String): List<String>

    /**
     * Find all [lexemes][LexemeEntity] whose [form][LexemeEntity.form] or
     * [property value][PropertyEntity.value] includes the given [query] string and meet a certain
     * restriction
     */
    @Transaction
    @Query(
        """SELECT id FROM dictionary_entries
                    WHERE (form LIKE '%' || (:query) || '%'
                    OR p_value LIKE '%' || (:query) || '%')
                    AND id IN (SELECT DISTINCT id FROM dictionary_entries
                    WHERE c_id = (:categoryId) AND p_value LIKE '%' || (:restriction) || '%')
                    AND c_hidden = 0
                    AND dictionary_id = (:dictionaryId)
                    GROUP BY id
                    ORDER BY form ASC"""
    )
    suspend fun findLexemes(
        dictionaryId: String,
        query: String,
        categoryId: String,
        restriction: String
    ): List<String>
}
