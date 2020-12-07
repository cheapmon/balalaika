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
import com.github.cheapmon.balalaika.model.Dictionary

/**
 * Database representation for a [dictionary][Dictionary]
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
)
