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
package com.github.cheapmon.balalaika.db.entities.lexeme

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.ui.bookmarks.BookmarksFragment
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryFragment

/**
 * Basic lexical unit
 *
 * For simplicity, we differentiate only between _simple lexemes_ and _full forms_. The only
 * difference is that full forms hold an additional [base][baseId] lexeme they depend on
 * (e.g. _happier_ points to _happy_). This can be applied to most languages.
 *
 * @see DictionaryFragment
 */
@Entity(
    primaryKeys = ["id", "dictionary_id"],
    foreignKeys = [
        ForeignKey(
            entity = Lexeme::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["base_id", "dictionary_id"]
        ),
        ForeignKey(
            entity = Dictionary::class,
            parentColumns = ["id"],
            childColumns = ["dictionary_id"]
        )
    ],
    indices = [Index("base_id", "dictionary_id")]
)
data class Lexeme(
    /** Identifier of this lexeme from sources */
    @ColumnInfo(index = true) val id: String,
    /** Dictionary this lexeme belongs to */
    @ColumnInfo(name = "dictionary_id", index = true) val dictionaryId: String,
    /**
     * Orthographic form of this lexeme
     *
     * This is the value displayed on top of the dictionary entry.
     */
    @ColumnInfo(name = "form") val form: String,
    /** Optional base of this lexeme */
    @ColumnInfo(name = "base_id", index = true) val baseId: String?,
    /**
     * Bookmark state of this lexeme
     *
     * @see BookmarksFragment
     */
    @ColumnInfo(name = "is_bookmark") val isBookmark: Boolean = false
)
