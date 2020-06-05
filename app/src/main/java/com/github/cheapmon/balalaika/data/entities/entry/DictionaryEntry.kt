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
package com.github.cheapmon.balalaika.data.entities.entry

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.entities.property.Property

@DatabaseView(
    """SELECT lexeme.*,
            base.id AS "b_id",
            base.external_id AS "b_external_id", 
            base.form AS "b_form", 
            base.base_id AS "b_base_id",
            base.is_bookmark AS "b_is_bookmark", 
            property.id AS "p_id", 
            property.category_id AS "p_category_id",
            property.lexeme_id AS "p_lexeme_id",
            property.value AS "p_value", 
            category.id AS "c_id", 
            category.external_id AS "c_external_id",
            category.name AS "c_name",
            category.widget AS "c_widget", 
            category.icon_id AS "c_icon_id", 
            category.sequence AS "c_sequence",
            category.hidden AS "c_hidden",
            category.order_by AS "c_order_by"
            FROM lexeme
            LEFT JOIN lexeme AS base ON lexeme.base_id = base.id
            LEFT JOIN property ON lexeme.id = property.lexeme_id 
            LEFT JOIN category ON property.category_id = category.id"""
)
data class DictionaryEntry(
    @Embedded val lexeme: Lexeme,
    @Embedded(prefix = "b_") val base: Lexeme?,
    @Embedded(prefix = "p_") val property: Property?,
    @Embedded(prefix = "c_") val category: Category?
)
