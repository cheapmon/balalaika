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

import com.github.cheapmon.balalaika.data.Mapper
import com.github.cheapmon.balalaika.data.dictionary.wordnet.WordnetInfo.LexicalEntry
import javax.inject.Inject
import org.apache.commons.text.StringEscapeUtils

class WordnetInfoMapper @Inject constructor() : Mapper<RDFNode, WordnetInfo> {
    private val prefix = "&wn;"

    override fun map(value: RDFNode) = WordnetInfo(
        entries = value.lexicalEntryList.orEmpty().map { node ->
            LexicalEntry(
                representation = StringEscapeUtils.unescapeHtml4(node.canonicalForm.writtenRep),
                partOfSpeech = when (node.partOfSpeech.resource.removePrefix(prefix)) {
                    "noun" -> "Noun"
                    "adjective" -> "Adjective"
                    "phrase" -> "Phrase"
                    "verb" -> "Verb"
                    "adverb" -> "Adverb"
                    "adjective_satellite" -> "Adjective Satellite"
                    else -> "Unknown"
                }
            )
        },
        definitions = value.lexicalConceptList.orEmpty().mapNotNull {
            val parts = StringEscapeUtils.unescapeHtml4(it.definition.value).split(Regex(";"))
            parts.firstOrNull()?.let { explanation ->
                val examples = parts.drop(1)
                    .map { e -> e.replace("\"", "").trim() }
                WordnetInfo.Definition(explanation, examples)
            }
        }
    )
}
