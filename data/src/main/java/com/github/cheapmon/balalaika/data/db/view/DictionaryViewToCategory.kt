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
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity

/**
 * Relation between [dictionary views][DictionaryViewEntity] and their associated
 * [data categories][CategoryEntity]
 */
@Entity(
    tableName = "dictionary_view_to_category",
    primaryKeys = ["dictionary_view_id", "category_id", "dictionary_id"],
    foreignKeys = [
        ForeignKey(
            entity = DictionaryViewEntity::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["dictionary_view_id", "dictionary_id"]
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["category_id", "dictionary_id"]
        ),
        ForeignKey(
            entity = DictionaryEntity::class,
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
    /** [Dictionary view][DictionaryViewEntity] */
    @ColumnInfo(name = "dictionary_view_id") val id: String,
    /** [CategoryEntity] associated with this dictionary view */
    @ColumnInfo(name = "category_id") val categoryId: String,
    /** [DictionaryEntity] associated with this dictionary view */
    @ColumnInfo(name = "dictionary_id", index = true) val dictionaryId: String
) : DatabaseEntity
