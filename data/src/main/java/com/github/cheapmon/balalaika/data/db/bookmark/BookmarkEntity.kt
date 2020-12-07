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
package com.github.cheapmon.balalaika.data.db.bookmark

import androidx.room.*
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.model.Bookmark

/**
 * Database representation of a [bookmark][Bookmark]
 *
 * @property id Unique identifier of this bookmark entity
 * @property dictionaryId Unique identifier of the dictionary this bookmark belongs to
 * @property lexemeId Unique identifier of the lexeme this bookmark belongs to
 *
 * @see Bookmark
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = LexemeEntity::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["lexeme_id", "dictionary_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DictionaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["dictionary_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("lexeme_id", "dictionary_id")],
    tableName = "bookmarks"
)
internal data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "dictionary_id", index = true) val dictionaryId: String,
    @ColumnInfo(name = "lexeme_id", index = true) val lexemeId: String
)
