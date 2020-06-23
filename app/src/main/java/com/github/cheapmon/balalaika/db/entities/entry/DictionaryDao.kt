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
package com.github.cheapmon.balalaika.db.entities.entry

import androidx.room.Dao
import androidx.room.Query
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.db.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.db.entities.property.Property
import com.github.cheapmon.balalaika.db.entities.property.PropertyDao
import com.github.cheapmon.balalaika.db.entities.view.DictionaryView
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryFragment
import com.github.cheapmon.balalaika.ui.search.SearchFragment

/**
 * Database link for [dictionary entries][DictionaryEntry]
 *
 * Dictionary entry retrieval consists of two steps:
 * 1. Querying of matching [lexemes][Lexeme]
 * 2. Fetching associated dictionary entries on demand
 *
 * In this data access object, we handle the first step by fetching
 * [lexeme identifiers][Lexeme.lexemeId] based on the user configuration.
 *
 * @see PropertyDao.getProperties
 */
@Dao
interface DictionaryDao {
    /**
     * Get all [lexeme ids][Lexeme.lexemeId], depending on a certain
     * [dictionary view][DictionaryView]
     *
     * This effectively checks for all [categories][Category] that are displayed within the
     * dictionary view and selects only those lexemes that match one of those categories and aren't
     * hidden.
     *
     * _Note_: These lexemes are sorted only by the [form][Lexeme.form] and
     * [sequence][Category.sequence] fields.
     *
     * @see DictionaryFragment
     */
    @Query(
        """SELECT id FROM PropertyDatabaseView
                    WHERE c_id IN (SELECT category_id FROM dictionary_view_to_category
                    WHERE dictionary_view_id = (:dictionaryViewId))
                    AND c_hidden = 0
                    GROUP BY id
                    ORDER BY form ASC, c_sequence ASC"""
    )
    suspend fun getLexemes(dictionaryViewId: Long): List<Long>

    /**
     * Get all [lexemes][Lexeme], depending on a certain [dictionary view][DictionaryView] and
     * sorted by a certain [data category][Category]
     *
     * This effectively checks for all [categories][Category] that are displayed within the
     * dictionary view and selects only those lexemes that match one of those categories and aren't
     * hidden, then sorts the remaining lexemes based on the given data category.
     *
     * @see DictionaryFragment
     */
    @Query(
        """SELECT id
                    FROM PropertyDatabaseView
                    JOIN (SELECT id AS o_id, p_value as o_value FROM PropertyDatabaseView
                    WHERE c_id = (:categoryId) ORDER BY p_value ASC) AS ids ON o_id = id
                    WHERE c_id IN (SELECT category_id FROM dictionary_view_to_category
                    WHERE dictionary_view_id = (:dictionaryViewId))
                    AND c_hidden = 0
                    GROUP BY id
                    ORDER BY o_value ASC, form ASC, c_sequence ASC"""
    )
    suspend fun getLexemes(
        dictionaryViewId: Long,
        categoryId: Long
    ): List<Long>

    /**
     * Find all [lexemes][Lexeme] whose [form][Lexeme.form] or
     * [property value][Property.value] includes the given [query] string
     *
     * @see SearchFragment
     */
    @Query(
        """SELECT id FROM PropertyDatabaseView
                    WHERE (form LIKE '%' || (:query) || '%'
                    OR p_value LIKE '%' || (:query) || '%')
                    AND c_hidden = 0
                    GROUP BY id
                    ORDER BY form ASC"""
    )
    suspend fun findLexemes(query: String): List<Long>

    /**
     * Find all [lexemes][Lexeme] whose [form][Lexeme.form] or
     * [property value][Property.value] includes the given [query] string and meet a certain
     * [restriction][SearchRestriction]
     *
     * @see SearchFragment
     */
    @Query(
        """SELECT id FROM PropertyDatabaseView
                    WHERE (form LIKE '%' || (:query) || '%'
                    OR p_value LIKE '%' || (:query) || '%')
                    AND id IN (SELECT DISTINCT id FROM PropertyDatabaseView
                    WHERE c_id = (:categoryId) AND p_value LIKE '%' || (:restriction) || '%')
                    AND c_hidden = 0
                    GROUP BY id
                    ORDER BY form ASC"""
    )
    suspend fun findLexemes(
        query: String,
        categoryId: Long,
        restriction: String
    ): List<Long>
}
