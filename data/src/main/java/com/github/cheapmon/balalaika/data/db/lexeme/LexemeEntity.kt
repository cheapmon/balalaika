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
package com.github.cheapmon.balalaika.data.db.lexeme

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity

/**
 * Basic lexical unit
 *
 * For simplicity, we differentiate only between _simple lexemes_ and _full forms_. The only
 * difference is that full forms hold an additional [base][baseId] lexeme they depend on
 * (e.g. _happier_ points to _happy_). This can be applied to most languages.
 */
@Entity(
    primaryKeys = ["id", "dictionary_id"],
    foreignKeys = [
        ForeignKey(
            entity = LexemeEntity::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["base_id", "dictionary_id"]
        ),
        ForeignKey(
            entity = DictionaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["dictionary_id"]
        )
    ],
    indices = [Index("base_id", "dictionary_id")],
    tableName = "lexemes"
)
internal data class LexemeEntity(
    /** Identifier of this lexeme from sources */
    @ColumnInfo(index = true) val id: String,
    /** Dictionary this lexeme belongs to */
    @ColumnInfo(name = "dictionary_id", index = true) val dictionaryId: String,
    /**
     * Orthographic form of this lexeme
     *
     * This is the value displayed on top of the dictionary entry.
     */
    val form: String,
    /** Optional base of this lexeme */
    @ColumnInfo(name = "base_id", index = true) val baseId: String?
)
