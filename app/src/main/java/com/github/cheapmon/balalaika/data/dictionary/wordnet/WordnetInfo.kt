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
package com.github.cheapmon.balalaika.data.dictionary.wordnet

/** Information about a word */
data class WordnetInfo(
    /** List of related words */
    val entries: List<LexicalEntry>,
    /** List of definitions */
    val definitions: List<Definition>
) {
    /** Related word */
    data class LexicalEntry(
        /** Written representation of this word */
        val representation: String,
        /** Part of speech of this word (e.g. noun) */
        val partOfSpeech: String
    )

    /** Definition of a word */
    data class Definition(
        /** Explanation */
        val explanation: String,
        /** Example sentences */
        val examples: List<String>
    )
}
