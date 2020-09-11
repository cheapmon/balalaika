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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = LexemeEntity::class,
            parentColumns = ["lexeme_id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DictionaryEntity::class,
            parentColumns = ["dictionary_id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    tableName = "bookmarks"
)
internal data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "dictionary_id", index = true) val dictionaryId: String,
    @ColumnInfo(name = "lexeme_id", index = true) val lexemeId: String
)
