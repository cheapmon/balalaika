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
package com.github.cheapmon.balalaika.db.entities.property

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.lexeme.Lexeme

/**
 * Property of a [lexeme][Lexeme] based on a [data category][Category]
 *
 * To keep data categories flexible, properties are not saved on the lexeme itself, but in a
 * different table. This also enables 1:n-associations.
 *
 * For additional information on Balalaika's data model, please refer to the
 * [Import documentation][com.github.cheapmon.balalaika.db.insert].
 */
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
    /**
     * Primary key of this property
     *
     * _Note_: A default value of `0` and `autoGenerate = true` effectively corresponds to
     * auto increment.
     */
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val propertyId: Long = 0,
    /** [Data category][Category] associated with this property */
    @ColumnInfo(name = "category_id") val categoryId: Long,
    /** [Lexeme] this property belongs to */
    @ColumnInfo(name = "lexeme_id") val lexemeId: Long,
    /** Serialized property value */
    @ColumnInfo(name = "value") val value: String
)
