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
package com.github.cheapmon.balalaika.data.entities.history

import androidx.room.*
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.ui.history.HistoryFragment

/**
 * Entry in search history
 *
 * An entry consists of a [query] and an optional restriction (fields [categoryId] and
 * [restriction]).
 *
 * @see SearchRestriction
 * @see HistoryFragment
 */
@Entity(
    tableName = "search_history",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"]
        )
    ],
    indices = [Index(value = ["category_id"])]
)
data class HistoryEntry(
    /**
     * Primary key of this history entry
     *
     * _Note_: A default value of `0` and `autoGenerate = true` effectively corresponds to
     * auto increment.
     */
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val historyEntryId: Long = 0,
    /** Optional [category][Category] of this entry if the search has been restricted */
    @ColumnInfo(name = "category_id") val categoryId: Long? = null,
    /** Optional restriction of this entry */
    @ColumnInfo(name = "restriction") val restriction: String? = null,
    /** Search query */
    @ColumnInfo(name = "query") val query: String
)
