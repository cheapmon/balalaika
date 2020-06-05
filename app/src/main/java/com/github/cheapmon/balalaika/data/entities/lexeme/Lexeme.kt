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
package com.github.cheapmon.balalaika.data.entities.lexeme

import androidx.room.*

@Entity(
    foreignKeys = [
        ForeignKey(entity = Lexeme::class, parentColumns = ["id"], childColumns = ["base_id"])
    ],
    indices = [
        Index(value = ["external_id"], unique = true),
        Index(value = ["base_id"])
    ]
)
data class Lexeme(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val lexemeId: Long = 0,
    @ColumnInfo(name = "external_id") val externalId: String,
    @ColumnInfo(name = "form") val form: String,
    @ColumnInfo(name = "base_id") val baseId: Long?,
    @ColumnInfo(name = "is_bookmark") val isBookmark: Boolean = false
)
