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

sealed class PropertyValue {
    data class Audio(val name: String, val fileName: String) : PropertyValue()
    data class Example(val name: String, val content: String) : PropertyValue()
    data class Morphology(val parts: List<String>) : PropertyValue()
    data class Plain(val value: String) : PropertyValue()
    data class Reference(val entry: DictionaryEntry) : PropertyValue()
    data class Simple(val value: String) : PropertyValue()
    data class Url(val name: String, val url: String) : PropertyValue()
    data class Wordnet(val name: String, val url: String) : PropertyValue()
}
