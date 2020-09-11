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
package com.github.cheapmon.balalaika.data.db.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity

/**
 * Item in search history
 *
 * An item consists of a [query] and an optional restriction (fields [categoryId] and
 * [restriction]).
 */
@Entity(
    tableName = "search_history",
    foreignKeys = [
        ForeignKey(
            entity = DictionaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["dictionary_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["category_id", "dictionary_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["category_id", "dictionary_id"])]
)
internal data class HistoryItemEntity(
    /**
     * Primary key of this history item
     *
     * _Note_: A default value of `0` and `autoGenerate = true` effectively corresponds to
     * auto increment.
     */
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Optional [category][CategoryEntity] of this item if the search has been restricted */
    @ColumnInfo(name = "category_id") val categoryId: String? = null,
    /** Optional [dictionary][DictionaryEntity] this item belongs to */
    @ColumnInfo(name = "dictionary_id", index = true) val dictionaryId: String,
    /** Optional restriction of this item */
    @ColumnInfo(name = "restriction") val restriction: String? = null,
    /** Search query */
    @ColumnInfo(name = "query") val query: String
)
