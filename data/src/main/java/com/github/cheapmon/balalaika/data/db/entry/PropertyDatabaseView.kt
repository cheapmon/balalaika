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

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.data.db.property.PropertyEntity

/**
 * Database View collecting all information associated with a single property of a dictionary
 * entry
 *
 * A single record in the view consists of a [lexeme][LexemeEntity], its [base][LexemeEntity], the
 * [property][PropertyEntity] itself, and its [data category][CategoryEntity].
 *
 * Most critical operations concerning dictionary entries use this view, since it is a lot easier
 * for filtering and ordering of entries to have everything in one place.
 *
 * _Note_: Since some of the linked data classes have fields with the same identifier in the
 * database, we need to rename each field and define prefixes to prevent conflicts, so that
 * Room can generate the correct code for this class.
 */
@DatabaseView(
    """SELECT lexemes.*,
            base.id AS "b_id",
            base.form AS "b_form", 
            base.base_id AS "b_base_id",
            properties.id AS "p_id", 
            properties.category_id AS "p_category_id",
            properties.lexeme_id AS "p_lexeme_id",
            properties.value AS "p_value", 
            categories.id AS "c_id", 
            categories.name AS "c_name",
            categories.widget AS "c_widget", 
            categories.icon_name AS "c_icon_name",
            categories.sequence AS "c_sequence",
            categories.hidden AS "c_hidden",
            categories.sortable AS "c_sortable",
            dictionaries.id AS "d_id",
            dictionaries.version AS "d_version",
            dictionaries.name AS "d_name",
            dictionaries.summary AS "d_summary",
            dictionaries.authors AS "d_authors",
            dictionaries.additional_info AS "d_addition_info"
            FROM lexemes
            LEFT JOIN lexemes AS base ON lexemes.base_id = base.id
            LEFT JOIN properties ON lexemes.id = properties.lexeme_id 
            LEFT JOIN categories ON properties.category_id = categories.id
            LEFT JOIN dictionaries ON categories.dictionary_id = dictionaries.id""",
    viewName = "dictionary_entries"
)
internal data class PropertyDatabaseView(
    /** [LexemeEntity] associated with [property] */
    @Embedded val lexeme: LexemeEntity,
    /** Base [lexeme][LexemeEntity] associated with [property] */
    @Embedded(prefix = "b_") val base: LexemeEntity?,
    /** Single [PropertyEntity] of a dictionary entry */
    @Embedded(prefix = "p_") val property: PropertyEntity?,
    /** [Data category][CategoryEntity] associated with [property] */
    @Embedded(prefix = "c_") val category: CategoryEntity?,
    /** [DictionaryEntity] associated with [property] */
    @Embedded(prefix = "d_") val dictionary: DictionaryEntity?
)
