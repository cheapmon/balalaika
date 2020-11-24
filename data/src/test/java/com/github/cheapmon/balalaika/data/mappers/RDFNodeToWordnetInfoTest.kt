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
package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.mappers.RDFNodeToWordnetInfo
import com.github.cheapmon.balalaika.data.repositories.wordnet.RDFNode
import com.github.cheapmon.balalaika.data.repositories.wordnet.RDFNode.*
import com.github.cheapmon.balalaika.model.WordnetInfo
import com.github.cheapmon.balalaika.model.WordnetInfo.Definition
import com.github.cheapmon.balalaika.model.WordnetInfo.LexicalEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class RDFNodeToWordnetInfoTest {
    private val mapper = RDFNodeToWordnetInfo()

    @Test
    fun `maps from RDF node`(): Unit = runBlockingTest {
        val node = RDFNode(
            lexicalEntryList = listOf(
                LexicalEntryNode(
                    canonicalForm = CanonicalFormNode(
                        writtenRep = "petitioner"
                    ),
                    partOfSpeech = PartOfSpeechNode(
                        resource = "&wn;noun"
                    )
                ),
                LexicalEntryNode(
                    canonicalForm = CanonicalFormNode(
                        writtenRep = "requester"
                    ),
                    partOfSpeech = PartOfSpeechNode(
                        resource = "&wn;noun"
                    )
                ),
                LexicalEntryNode(
                    canonicalForm = CanonicalFormNode(
                        writtenRep = "suer"
                    ),
                    partOfSpeech = PartOfSpeechNode(
                        resource = "&wn;noun"
                    )
                ),
                LexicalEntryNode(
                    canonicalForm = CanonicalFormNode(
                        writtenRep = "suppliant"
                    ),
                    partOfSpeech = PartOfSpeechNode(
                        resource = "&wn;noun"
                    )
                ),
                LexicalEntryNode(
                    canonicalForm = CanonicalFormNode(
                        writtenRep = "supplicant"
                    ),
                    partOfSpeech = PartOfSpeechNode(
                        resource = "&wn;noun"
                    )
                )
            ), lexicalConceptList = listOf(
                LexicalConceptNode(
                    definition = DefinitionNode(
                        value = "one praying humbly for something; &quot;a suppliant for her favors&quot;"
                    )
                ),
                LexicalConceptNode(
                    definition = DefinitionNode(
                        value = "someone who petitions a court for redress of a grievance or recovery of a right"
                    )
                )
            )
        )
        val wordnetInfo = mapper(node)
        assertEquals(
            WordnetInfo(
                entries = listOf(
                    LexicalEntry(
                        representation = "petitioner",
                        partOfSpeech = "Noun"
                    ), LexicalEntry(
                        representation = "requester",
                        partOfSpeech = "Noun"
                    ), LexicalEntry(
                        representation = "suer",
                        partOfSpeech = "Noun"
                    ), LexicalEntry(
                        representation = "suppliant",
                        partOfSpeech = "Noun"
                    ), LexicalEntry(
                        representation = "supplicant",
                        partOfSpeech = "Noun"
                    )
                ),
                definitions = listOf(
                    Definition(
                        explanation = "one praying humbly for something",
                        examples = listOf(
                            "a suppliant for her favors"
                        )
                    ),
                    Definition(
                        explanation = "someone who petitions a court for redress of a grievance or recovery of a right",
                        examples = emptyList()
                    )
                )
            ),
            wordnetInfo
        )
    }
}
