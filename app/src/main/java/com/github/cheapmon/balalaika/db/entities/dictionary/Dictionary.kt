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
package com.github.cheapmon.balalaika.db.entities.dictionary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dictionary")
data class Dictionary(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val dictionaryId: Long = 0,
    @ColumnInfo(name = "external_id") val externalId: String = "",
    val version: Int = 0,
    val name: String = "",
    val summary: String = "",
    val authors: String = "",
    @ColumnInfo(name = "additional_info") val additionalInfo: String = "",
    val url: String? = null,
    @ColumnInfo(name = "is_active") val isActive: Boolean = false
)
