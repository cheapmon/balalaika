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
package com.github.cheapmon.balalaika.data.entities.lexeme

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Database link for [lexemes][Lexeme] */
@Dao
interface LexemeDao {
    /** Get all bookmarked [lexemes][Lexeme] */
    @Query("SELECT * FROM lexeme WHERE is_bookmark = 1")
    fun getBookmarks(): Flow<List<Lexeme>>

    /** Toggle bookmark state for a [lexeme][Lexeme] */
    @Query("UPDATE lexeme SET is_bookmark = NOT is_bookmark WHERE id = (:id)")
    suspend fun toggleBookmark(id: Long)

    /** Remove all bookmarks */
    @Query("UPDATE lexeme SET is_bookmark = 0 WHERE is_bookmark = 1")
    suspend fun clearBookmarks()

    /** Insert all [lexemes][Lexeme] into the database */
    @Insert
    suspend fun insertAll(vararg lexemes: Lexeme)
}