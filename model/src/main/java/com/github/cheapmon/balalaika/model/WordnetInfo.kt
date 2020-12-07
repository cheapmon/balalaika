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
 * Information about a word from the Wordnet project
 *
 * @property entries List of related lexical entries associated with a word
 * @property definitions List of definitions for a word
 */
data class WordnetInfo(
    val entries: List<LexicalEntry>,
    val definitions: List<Definition>
) {
    /**
     * Related word
     *
     * @property representation Representation of the word
     * @property partOfSpeech Part-of-speech of the word
     */
    data class LexicalEntry(
        val representation: String,
        val partOfSpeech: String
    )

    /**
     * Definition of a word
     *
     * @property explanation Explanation text of the definition
     * @property examples List of example sentences for the definition
     */
    data class Definition(
        val explanation: String,
        val examples: List<String>
    )
}
