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
package com.github.cheapmon.balalaika.data.insert

import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.entities.property.Property
import com.github.cheapmon.balalaika.data.entities.view.DictionaryView
import com.github.cheapmon.balalaika.data.entities.view.DictionaryViewToCategory

/** Component which extracts database entities from a source */
interface EntityImporter {
    /** Extract [categories][Category] from source files */
    fun readCategories(): List<Category>

    /** Extract [lexemes][Lexeme] from source files */
    fun readLexemes(): List<Lexeme>

    /** Extract [properties][Property] from source files */
    fun readProperties(): List<Property>

    /** Extract [dictionary views][DictionaryView] from source files */
    fun readDictionaryViews(): List<DictionaryView>

    /** Extract [dictionary view relations][DictionaryViewToCategory] from source files */
    fun readDictionaryViewToCategories(): List<DictionaryViewToCategory>
}
