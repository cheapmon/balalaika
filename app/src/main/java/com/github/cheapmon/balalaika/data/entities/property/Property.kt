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
package com.github.cheapmon.balalaika.data.entities.property

import androidx.room.*
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"]
        ),
        ForeignKey(entity = Lexeme::class, parentColumns = ["id"], childColumns = ["lexeme_id"])
    ],
    indices = [Index(value = ["category_id"]), Index(value = ["lexeme_id"])]
)
data class Property(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val propertyId: Long = 0,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "lexeme_id") val lexemeId: Long,
    @ColumnInfo(name = "value") val value: String
)
