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

// TODO: Make second component optional
sealed class Property {
    data class Audio(val name: String, val fileName: String) : Property()
    data class Example(val name: String, val content: String) : Property()
    data class Morphology(val parts: List<String>) : Property()
    data class Plain(val value: String) : Property()
    data class Reference(val entry: DictionaryEntry) : Property()
    data class Simple(val value: String) : Property()
    data class Url(val name: String, val url: String) : Property()
    data class Wordnet(val name: String, val url: String) : Property()
}
