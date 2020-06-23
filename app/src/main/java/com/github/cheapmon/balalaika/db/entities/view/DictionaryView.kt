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
import androidx.room.PrimaryKey
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.entry.PropertyDatabaseView

/**
 * Limited view on dictionary entries
 *
 * A dictionary view essentially gives a name to a subset of all available
 * [data categories][Category] with a certain task in mind.
 * Selecting a dictionary view in the user interface results in only a certain number of properties
 * being shown.
 * For example, for certain translation tasks it may suffice to only show the lexeme and the
 * translation itself.
 *
 * For additional information on Balalaika's data model, please refer to the
 * [Import documentation][com.github.cheapmon.balalaika.db.insert].
 *
 * @see PropertyDatabaseView
 */
@Entity(tableName = "dictionary_view")
data class DictionaryView(
    /**
     * Primary key of this dictionary view
     *
     * _Note_: A default value of `0` and `autoGenerate = true` effectively corresponds to
     * auto increment.
     */
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val dictionaryViewId: Long = 0,
    /** Unique identifier of this dictionary view from sources */
    @ColumnInfo(name = "external_id") val externalId: String,
    /** Name of this dictionary view */
    @ColumnInfo(name = "name") val name: String
)
