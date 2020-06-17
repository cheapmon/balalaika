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
package com.github.cheapmon.balalaika.data.entities.cache

import androidx.room.*
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.repositories.SearchPagingSource

/**
 * Entry in the search cache
 *
 * @see SearchPagingSource
 */
@Entity(tableName = "search_cache_entry", indices = [Index(value = ["lexeme_id"])])
data class SearchCacheEntry(
    /** Primary key of this entry
     *
     * This effectively serves as the relative position of a lexeme in the dictionary.
     */
    @PrimaryKey @ColumnInfo(name = "id") val searchCacheEntryId: Long,
    /** Identifier of a [lexeme][Lexeme] */
    @ForeignKey(entity = Lexeme::class, parentColumns = ["id"], childColumns = ["lexeme_id"])
    @ColumnInfo(name = "lexeme_id")
    val lexemeId: Long
)