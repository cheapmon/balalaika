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
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity

/**
 * Limited view on dictionary entries
 *
 * A dictionary view essentially gives a name to a subset of all available
 * [data categories][CategoryEntity] with a certain task in mind.
 * Selecting a dictionary view in the user interface results in only a certain number of properties
 * being shown.
 * For example, for certain translation tasks it may suffice to only show the lexeme and the
 * translation itself.
 */
@Entity(
    tableName = "dictionary_views",
    primaryKeys = ["id", "dictionary_id"],
    foreignKeys = [
        ForeignKey(
            entity = DictionaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["dictionary_id"]
        )
    ]
)
internal data class DictionaryViewEntity(
    /** Identifier of this dictionary view from sources */
    val id: String,
    /** Dictionary this view belongs to */
    @ColumnInfo(name = "dictionary_id", index = true) val dictionaryId: String,
    /** Name of this dictionary view */
    val name: String
)
