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
package com.github.cheapmon.balalaika.data.db.bookmark

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Database link for bookmarks */
@Dao
internal interface BookmarkDao {
    /** Find a bookmark for a [lexeme][lexemeId] in a [dictionary][dictionaryId] */
    @Query(
        """SELECT * FROM bookmarks
                WHERE dictionary_id = (:dictionaryId) AND lexeme_id = (:lexemeId)
                LIMIT 1"""
    )
    suspend fun findById(dictionaryId: String, lexemeId: String): BookmarkEntity?

    /** Get all bookmarks in a [dictionary][dictionaryId] */
    @Query("SELECT * FROM bookmarks WHERE dictionary_id = (:dictionaryId)")
    fun getAll(dictionaryId: String): Flow<List<BookmarkEntity>>

    /** Insert a bookmark for a lexeme */
    @Insert
    suspend fun insert(bookmark: BookmarkEntity)

    /** Remove a bookmark for a [lexeme][lexemeId] in a [dictionary][dictionaryId] */
    @Query(
        """DELETE FROM bookmarks
                WHERE lexeme_id = (:lexemeId) AND dictionary_id = (:dictionaryId)"""
    )
    suspend fun remove(dictionaryId: String, lexemeId: String)

    /** Remove all bookmarks for a [dictionary][dictionaryId] */
    @Query(
        """DELETE FROM bookmarks
                WHERE dictionary_id = (:dictionaryId)"""
    )
    suspend fun removeInDictionary(dictionaryId: String)

    /** Total number of bookmarks */
    @Query("SELECT COUNT(*) FROM bookmarks")
    suspend fun count(): Int
}
