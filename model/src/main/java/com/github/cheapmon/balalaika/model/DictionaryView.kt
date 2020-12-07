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
package com.github.cheapmon.balalaika.model

/**
 * Limited view on dictionary entries
 *
 * A dictionary view essentially gives a name to a subset of all available
 * [data categories][DataCategory] with a certain task in mind. Selecting a dictionary view in the
 * user interface results in only a certain number of properties being shown. For example, for
 * certain translation tasks it may suffice to only show the lexeme and the translation itself.
 *
 * @property id Unique identifier for this dictionary view
 * @property name Display name of this dictionary view
 * @property categories List of data categories included in this dictionary view
 *
 * @see DataCategory
 */
data class DictionaryView(
    val id: String,
    val name: String,
    val categories: List<DataCategory>
)
