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
package com.github.cheapmon.balalaika.data.entities.entry

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.entities.property.Property
import com.github.cheapmon.balalaika.data.entities.view.DictionaryView
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryFragment
import com.github.cheapmon.balalaika.ui.search.SearchFragment
import kotlinx.coroutines.flow.Flow

/**
 * Database link for [dictionary entries][DictionaryEntry]
 *
 * Dictionary entry retrieval consists of two steps:
 * 1. Querying of matching [lexemes][Lexeme] (e.g. [getLexemesFiltered], [getLexemesSorted],
 * [findLexemes])
 * 2. Fetching associated dictionary entries on demand (e.g. [getFiltered], [getSorted], [find])
 *
 * Using this indirection, we enable
 * [paging](https://developer.android.com/topic/libraries/architecture/paging)
 * and ensure that only a small number of entries is loaded at the same time.
 */
@Dao
interface DictionaryEntryDao {
    /**
     * Get all [dictionary entries][DictionaryEntry] for a single [lexeme][Lexeme], depending on a
     * certain [dictionary view][DictionaryView]
     *
     * This effectively checks for all [categories][Category] that are displayed within the
     * dictionary view and selects only those entries that match one of those categories and aren't
     * hidden.
     *
     * _Note_: These entries are sorted only by the [form][Lexeme.form] and
     * [sequence][Category.sequence] fields.
     * More complex sorting is achieved using [getSorted].
     *
     * @see DictionaryFragment
     */
    @Query(
        """SELECT * FROM DictionaryEntry
                    WHERE id = (:lexemeId)
                    AND c_id IN (SELECT category_id FROM dictionary_view_to_category
                    WHERE dictionary_view_id = (:dictionaryViewId))
                    AND c_hidden = 0
                    ORDER BY form ASC, c_sequence ASC"""
    )
    suspend fun getFiltered(dictionaryViewId: Long, lexemeId: Long): List<DictionaryEntry>

    /**
     * Get all [lexemes][Lexeme], depending on a certain [dictionary view][DictionaryView]
     *
     * This effectively checks for all [categories][Category] that are displayed within the
     * dictionary view and selects only those lexemes that match one of those categories and aren't
     * hidden.
     *
     * _Note_: These lexemes are sorted only by the [form][Lexeme.form] and
     * [sequence][Category.sequence] fields.
     * More complex sorting is achieved using [getLexemesSorted].
     *
     * @see DictionaryFragment
     */
    @Query(
        """SELECT id, external_id, form, base_id, is_bookmark FROM DictionaryEntry
                    WHERE c_id IN (SELECT category_id FROM dictionary_view_to_category
                    WHERE dictionary_view_id = (:dictionaryViewId))
                    AND c_hidden = 0
                    GROUP BY id
                    ORDER BY form ASC, c_sequence ASC"""
    )
    fun getLexemesFiltered(dictionaryViewId: Long): DataSource.Factory<Int, Lexeme>

    /**
     * Get all [external identifiers][Lexeme.externalId] of [lexemes][Lexeme], depending on a
     * certain [dictionary view][DictionaryView]
     *
     * This effectively checks for all [categories][Category] that are displayed within the
     * dictionary view and selects only those identifiers that match one of those categories and
     * aren't hidden.
     *
     * The result is similar to the result of [getFiltered], expect this function only returns
     * identifiers of [dictionary entries][DictionaryEntry]. This can be used to determine the
     * position of a lexeme in the entry list.
     *
     * _Note_: These identifiers are sorted only by the [form][Lexeme.form] and
     * [sequence][Category.sequence] fields.
     * More complex sorting is achieved using [getIdsSorted].
     *
     * @see DictionaryFragment
     */
    @Query(
        """SELECT external_id FROM DictionaryEntry
                    WHERE c_id IN (SELECT category_id FROM dictionary_view_to_category
                    WHERE dictionary_view_id = (:dictionaryViewId))
                    AND c_hidden = 0
                    GROUP BY id
                    ORDER BY form ASC, c_sequence ASC"""
    )
    fun getIdsFiltered(dictionaryViewId: Long): Flow<List<String>>

    /**
     * Get all [dictionary entries][DictionaryEntry] for a single [lexeme][Lexeme], depending on a
     * certain [dictionary view][DictionaryView] and sorted by a certain [data category][Category]
     *
     * This effectively checks for all [categories][Category] that are displayed within the
     * dictionary view and selects only those entries that match one of those categories, have the
     * given [lexemeId] and aren't hidden, then sorts the remaining entries based on the given data
     * category.
     *
     * @see DictionaryFragment
     */
    @Query(
        """SELECT id, external_id, form, base_id, is_bookmark, b_id, b_external_id, b_form,
                    b_base_id, b_is_bookmark, p_id, p_category_id, p_lexeme_id, p_value, c_id,
                    c_external_id, c_name, c_widget, c_icon_id, c_sequence, c_hidden, c_order_by
                    FROM DictionaryEntry
                    JOIN (SELECT id AS o_id, p_value as o_value FROM DictionaryEntry
                    WHERE c_id = (:categoryId) ORDER BY p_value ASC) AS ids ON o_id = id
                    WHERE id = (:lexemeId)
                    AND c_id IN (SELECT category_id FROM dictionary_view_to_category
                    WHERE dictionary_view_id = (:dictionaryViewId))
                    AND c_hidden = 0
                    ORDER BY o_value ASC, form ASC, c_sequence ASC"""
    )
    suspend fun getSorted(
        dictionaryViewId: Long,
        categoryId: Long,
        lexemeId: Long
    ): List<DictionaryEntry>

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
        """SELECT id, external_id, form, base_id, is_bookmark
                    FROM DictionaryEntry
                    JOIN (SELECT id AS o_id, p_value as o_value FROM DictionaryEntry
                    WHERE c_id = (:categoryId) ORDER BY p_value ASC) AS ids ON o_id = id
                    WHERE c_id IN (SELECT category_id FROM dictionary_view_to_category
                    WHERE dictionary_view_id = (:dictionaryViewId))
                    AND c_hidden = 0
                    GROUP BY id
                    ORDER BY o_value ASC, form ASC, c_sequence ASC"""
    )
    fun getLexemesSorted(
        dictionaryViewId: Long,
        categoryId: Long
    ): DataSource.Factory<Int, Lexeme>

    /**
     * Get all [external identifiers][Lexeme.externalId] of [lexemes][Lexeme], depending on a
     * certain [dictionary view][DictionaryView] and sorted by a certain [data category][Category]
     *
     * This effectively checks for all [categories][Category] that are displayed within the
     * dictionary view and selects only those identifiers that match one of those categories and
     * aren't hidden, then sorts the remaining lexemes based on the given data category.
     *
     * The result is similar to the result of [getSorted], expect this function only returns
     * identifiers of [dictionary entries][DictionaryEntry]. This can be used to determine the
     * position of a lexeme in the entry list.
     *
     * @see DictionaryFragment
     */
    @Query(
        """SELECT external_id FROM DictionaryEntry
                    JOIN (SELECT id AS o_id, p_value as o_value FROM DictionaryEntry
                    WHERE c_id = (:categoryId) ORDER BY p_value ASC) AS ids ON o_id = id
                    WHERE c_id IN (SELECT category_id FROM dictionary_view_to_category
                    WHERE dictionary_view_id = (:dictionaryViewId))
                    AND c_hidden = 0
                    GROUP BY id
                    ORDER BY o_value ASC, form ASC, c_sequence ASC"""
    )
    fun getIdsSorted(
        dictionaryViewId: Long,
        categoryId: Long
    ): Flow<List<String>>

    /**
     * Find all [dictionary entries][DictionaryEntry] whose [form][Lexeme.form] or
     * [property value][Property.value] includes the given [query] string
     *
     * @see SearchFragment
     */
    @Query(
        """SELECT * FROM DictionaryEntry
                    WHERE id = (:lexemeId)
                    AND (form LIKE '%' || (:query) || '%'
                    OR p_value LIKE '%' || (:query) || '%')
                    AND c_hidden = 0
                    ORDER BY form ASC"""
    )
    suspend fun find(query: String, lexemeId: Long): List<DictionaryEntry>

    /**
     * Find all [lexemes][Lexeme] whose [form][Lexeme.form] or
     * [property value][Property.value] includes the given [query] string
     *
     * @see SearchFragment
     */
    @Query(
        """SELECT id, external_id, form, base_id, is_bookmark FROM DictionaryEntry
                    WHERE (form LIKE '%' || (:query) || '%'
                    OR p_value LIKE '%' || (:query) || '%')
                    AND c_hidden = 0
                    GROUP BY id
                    ORDER BY form ASC"""
    )
    fun findLexemes(query: String): DataSource.Factory<Int, Lexeme>

    /**
     * Find all [dictionary entries][DictionaryEntry] whose [form][Lexeme.form] or
     * [property value][Property.value] includes the given [query] string and meet a certain
     * [restriction][SearchRestriction]
     *
     * @see SearchFragment
     */
    @Query(
        """SELECT * FROM DictionaryEntry
                    WHERE id = (:lexemeId)
                    AND (form LIKE '%' || (:query) || '%'
                    OR p_value LIKE '%' || (:query) || '%')
                    AND id IN (SELECT DISTINCT id FROM DictionaryEntry
                    WHERE c_id = (:categoryId) AND p_value LIKE '%' || (:restriction) || '%')
                    AND c_hidden = 0
                    ORDER BY form ASC"""
    )
    suspend fun findWith(
        query: String,
        categoryId: Long,
        restriction: String,
        lexemeId: Long
    ): List<DictionaryEntry>

    /**
     * Find all [lexemes][Lexeme] whose [form][Lexeme.form] or
     * [property value][Property.value] includes the given [query] string and meet a certain
     * [restriction][SearchRestriction]
     *
     * @see SearchFragment
     */
    @Query(
        """SELECT id, external_id, form, base_id, is_bookmark FROM DictionaryEntry
                    WHERE (form LIKE '%' || (:query) || '%'
                    OR p_value LIKE '%' || (:query) || '%')
                    AND id IN (SELECT DISTINCT id FROM DictionaryEntry
                    WHERE c_id = (:categoryId) AND p_value LIKE '%' || (:restriction) || '%')
                    AND c_hidden = 0
                    GROUP BY id
                    ORDER BY form ASC"""
    )
    fun findLexemesWith(
        query: String,
        categoryId: Long,
        restriction: String
    ): DataSource.Factory<Int, Lexeme>
}