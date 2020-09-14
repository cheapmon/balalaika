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
package com.github.cheapmon.balalaika.data.db.entry

import androidx.room.Embedded
import androidx.room.Relation
import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.data.db.property.PropertyEntity
import com.github.cheapmon.balalaika.data.db.property.PropertyWithCategory

/**
 * Full entry in a dictionary, consisting of a [lexeme][LexemeEntity] grouped with its base and all of
 * its [properties][PropertyWithCategory]
 */
internal data class DictionaryEntryEntity(
    /** A single lexeme */
    @Embedded val lexeme: LexemeEntity,

    /** The base associated with this lexeme */
    @Relation(
        parentColumn = "base_id",
        entityColumn = "id"
    ) val base: LexemeEntity?,

    /** All properties of this lexeme */
    @Relation(
        entity = PropertyEntity::class,
        parentColumn = "id",
        entityColumn = "lexeme_id"
    )
    val properties: List<PropertyWithCategory>,

    @Relation(
        parentColumn = "id",
        entityColumn = "lexeme_id"
    )
    val bookmark: BookmarkEntity?
)
