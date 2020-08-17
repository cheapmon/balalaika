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
package com.github.cheapmon.balalaika.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.github.cheapmon.balalaika.data.db.DatabaseEntity
import com.github.cheapmon.balalaika.data.db.category.Category
import com.github.cheapmon.balalaika.data.db.dictionary.Dictionary

/**
 * Relation between [dictionary views][DictionaryView] and their associated
 * [data categories][Category]
 */
@Entity(
    tableName = "dictionary_view_to_category",
    primaryKeys = ["dictionary_view_id", "category_id", "dictionary_id"],
    foreignKeys = [
        ForeignKey(
            entity = DictionaryView::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["dictionary_view_id", "dictionary_id"]
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["category_id", "dictionary_id"]
        ),
        ForeignKey(
            entity = Dictionary::class,
            parentColumns = ["id"],
            childColumns = ["dictionary_id"]
        )
    ],
    indices = [
        Index("dictionary_view_id", "dictionary_id"),
        Index("category_id", "dictionary_id")
    ]
)
internal data class DictionaryViewToCategory(
    /** [Dictionary view][DictionaryView] */
    @ColumnInfo(name = "dictionary_view_id") val id: String,
    /** [Category] associated with this dictionary view */
    @ColumnInfo(name = "category_id") val categoryId: String,
    /** [Dictionary] associated with this dictionary view */
    @ColumnInfo(name = "dictionary_id", index = true) val dictionaryId: String
) : DatabaseEntity
