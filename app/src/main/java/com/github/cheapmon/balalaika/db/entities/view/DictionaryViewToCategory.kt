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
package com.github.cheapmon.balalaika.db.entities.view

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.github.cheapmon.balalaika.db.entities.category.Category

/**
 * Relation between [dictionary views][DictionaryView] and their associated
 * [data categories][Category]
 */
@Entity(
    tableName = "dictionary_view_to_category",
    primaryKeys = ["dictionary_view_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = DictionaryView::class,
            parentColumns = ["id"],
            childColumns = ["dictionary_view_id"]
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"]
        )
    ],
    indices = [Index(value = ["dictionary_view_id"]), Index(
        value = ["category_id"]
    )]
)
data class DictionaryViewToCategory(
    /** [Dictionary view][DictionaryView] */
    @ColumnInfo(name = "dictionary_view_id") val dictionaryViewId: Long,
    /** [Category] associated with this dictionary view */
    @ColumnInfo(name = "category_id") val categoryId: String
)
