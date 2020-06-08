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

import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.entities.property.PropertyWithRelations

/**
 * [Lexeme] grouped with all of its [properties][PropertyWithRelations]
 */
data class GroupedEntry(
    /** [Lexeme] associated with this entry */
    val lexeme: Lexeme,
    /** Base [lexeme][Lexeme] associated with this entry */
    val base: Lexeme?,
    /** All [properties][PropertyWithRelations] associated with [lexeme] */
    val properties: List<PropertyWithRelations>
)
