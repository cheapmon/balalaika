package com.github.cheapmon.balalaika.data.entities.entry

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryEntryDao {
    @Query(
        """SELECT id, external_id, form, base_id, is_bookmark
                    FROM DictionaryEntry
                    WHERE is_bookmark = 1"""
    )
    fun getBookmarked(): DataSource.Factory<Int, Lexeme>

    @Query(
        """SELECT * FROM DictionaryEntry
                    WHERE id = (:lexemeId)
                    AND c_id IN (SELECT category_id FROM dictionary_view_to_category
                    WHERE dictionary_view_id = (:dictionaryViewId))
                    AND c_hidden = 0
                    ORDER BY form ASC, c_sequence ASC"""
    )
    suspend fun getFiltered(dictionaryViewId: Long, lexemeId: Long): List<DictionaryEntry>

    @Query(
        """SELECT id, external_id, form, base_id, is_bookmark FROM DictionaryEntry
                    WHERE c_id IN (SELECT category_id FROM dictionary_view_to_category
                    WHERE dictionary_view_id = (:dictionaryViewId))
                    AND c_hidden = 0
                    GROUP BY id
                    ORDER BY form ASC, c_sequence ASC"""
    )
    fun getLexemesFiltered(dictionaryViewId: Long): DataSource.Factory<Int, Lexeme>

    @Query(
        """SELECT external_id FROM DictionaryEntry
                    WHERE c_id IN (SELECT category_id FROM dictionary_view_to_category
                    WHERE dictionary_view_id = (:dictionaryViewId))
                    AND c_hidden = 0
                    GROUP BY id
                    ORDER BY form ASC, c_sequence ASC"""
    )
    fun getIdsFiltered(dictionaryViewId: Long): Flow<List<String>>

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

    @Query(
        """SELECT * FROM DictionaryEntry
                    WHERE id = (:lexemeId)
                    AND (form LIKE '%' || (:query) || '%'
                    OR p_value LIKE '%' || (:query) || '%')
                    AND c_hidden = 0
                    ORDER BY form ASC"""
    )
    suspend fun find(query: String, lexemeId: Long): List<DictionaryEntry>

    @Query(
        """SELECT id, external_id, form, base_id, is_bookmark FROM DictionaryEntry
                    WHERE (form LIKE '%' || (:query) || '%'
                    OR p_value LIKE '%' || (:query) || '%')
                    AND c_hidden = 0
                    GROUP BY id
                    ORDER BY form ASC"""
    )
    fun findLexemes(query: String): DataSource.Factory<Int, Lexeme>

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