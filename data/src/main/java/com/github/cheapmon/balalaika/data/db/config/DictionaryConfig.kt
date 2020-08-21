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
package com.github.cheapmon.balalaika.data.db.config

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity

/**
 * Application configuration for the display of a [dictionary][DictionaryEntity]
 *
 * When another dictionary is activated, two pieces of information need to be saved:
 * - The selected [dictionary view][DictionaryViewEntity]
 * - The selected [category][CategoryEntity] to order dictionary entries by
 *
 * For each dictionary, there always exists one dictionary configuration.
 */
@Entity(
    tableName = "configurations",
    foreignKeys = [
        ForeignKey(
            entity = DictionaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"]
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["sort_by", "id"]
        ),
        ForeignKey(
            entity = DictionaryViewEntity::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["filter_by", "id"]
        )
    ],
    indices = [
        Index(value = ["sort_by", "id"]),
        Index(value = ["filter_by", "id"])
    ]
)
internal data class DictionaryConfig(
    /** [DictionaryEntity] this configuration belongs to */
    @PrimaryKey val id: String,
    /** [CategoryEntity] identifier of this configuration */
    @ColumnInfo(name = "sort_by", index = true) val sortBy: String,
    /** [DictionaryViewEntity] identifier of this configuration */
    @ColumnInfo(name = "filter_by", index = true) val filterBy: String
)
