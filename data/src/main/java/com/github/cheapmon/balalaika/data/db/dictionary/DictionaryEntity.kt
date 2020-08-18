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
package com.github.cheapmon.balalaika.data.db.dictionary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.cheapmon.balalaika.data.db.DatabaseEntity

/**
 * Description and metadata for a dictionary
 *
 * Encapsulates information about a dictionary from any source in the user interface. Instances of
 * this class are provided by some local or remote sources and saved into the
 * database.
 *
 * Dictionaries are identified by their [id], which must be unique across all sources, local or
 * remote. Using this approach, we enable dictionaries to be included in the application bundle
 * and to be updated from a remote source.
 *
 * Invalidation of dictionaries is achieved via the [version] number. There is currently no way
 * implemented to compare the contents of two dictionaries with the same [id].
 */
@Entity(tableName = "dictionaries")
internal data class DictionaryEntity(
    /** Unique primary identifier of this dictionary */
    @PrimaryKey val id: String,
    /** Version number of this dictionary */
    val version: Int,
    /** Name of this dictionary */
    val name: String,
    /** Summary text for this dictionary */
    val summary: String,
    /** Authors of this dictionary */
    val authors: String,
    /** Additional information for this dictionary (e.g. hyperlinks) */
    @ColumnInfo(name = "additional_info") val additionalInfo: String
) : DatabaseEntity
