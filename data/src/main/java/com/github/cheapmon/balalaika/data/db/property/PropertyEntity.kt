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
package com.github.cheapmon.balalaika.data.db.property

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.cheapmon.balalaika.data.db.DatabaseEntity
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity

/**
 * Property of a [lexeme][LexemeEntity] based on a [data category][CategoryEntity]
 *
 * To keep data categories flexible, properties are not saved on the lexeme itself, but in a
 * different table. This also enables 1:n-associations.
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["category_id", "dictionary_id"]
        ),
        ForeignKey(
            entity = DictionaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["dictionary_id"]
        ),
        ForeignKey(
            entity = LexemeEntity::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["lexeme_id", "dictionary_id"]
        )
    ],
    indices = [
        Index(value = ["category_id", "dictionary_id"]),
        Index(value = ["lexeme_id", "dictionary_id"])
    ],
    tableName = "properties"
)
internal data class PropertyEntity(
    /**
     * Primary key of this property
     *
     * _Note_: A default value of `0` and `autoGenerate = true` effectively corresponds to
     * auto increment.
     */
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** [Data category][CategoryEntity] associated with this property */
    @ColumnInfo(name = "category_id") val categoryId: String,
    /** [DictionaryEntity] associated with this property */
    @ColumnInfo(name = "dictionary_id", index = true) val dictionaryId: String,
    /** [LexemeEntity] this property belongs to */
    @ColumnInfo(name = "lexeme_id") val lexemeId: String,
    /** Serialized property value */
    val value: String
) : DatabaseEntity
