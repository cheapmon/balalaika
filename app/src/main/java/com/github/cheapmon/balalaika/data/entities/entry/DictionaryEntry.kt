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
import com.github.cheapmon.balalaika.data.entities.lexeme.LexemeWithBase
import com.github.cheapmon.balalaika.data.entities.property.PropertyWithCategory

/**
 * Full entry in a dictionary, consisting of a [lexeme][Lexeme] grouped with its base and all of
 * its [properties][PropertyWithCategory]
 */
data class DictionaryEntry(
    /** [Lexeme] and [base][Lexeme.baseId] associated with this entry */
    val lexemeWithBase: LexemeWithBase,
    /** All [properties][PropertyWithCategory] associated with [lexemeWithBase] */
    val properties: List<PropertyWithCategory>
)
