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

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root

@Root(name = "RDF", strict = false)
@Namespace(prefix = "rdf")
data class RDFNode(
    @field:ElementList(inline = true)
    var lexicalEntryList: List<LexicalEntryNode> = mutableListOf(),

    @field:ElementList(inline = true)
    var lexicalConceptList: List<LexicalConceptNode> = mutableListOf()
) {
    @Root(name = "LexicalEntry", strict = false)
    @Namespace(prefix = "ontolex")
    data class LexicalEntryNode(
        @field:Element
        var canonicalForm: CanonicalFormNode? = null,

        @field:Element
        var partOfSpeech: PartOfSpeechNode? = null
    )

    @Root(name = "canonicalForm", strict = false)
    @Namespace(prefix = "ontolex")
    data class CanonicalFormNode(
        @field:Element
        var writtenRep: String = ""
    )

    @Root(name = "partOfSpeech", strict = false)
    @Namespace(prefix = "wn")
    data class PartOfSpeechNode(
        @field:Attribute
        @Namespace(prefix = "rdf")
        var resource: String = ""
    )

    @Root(name = "LexicalConcept", strict = false)
    @Namespace(prefix = "ontolex")
    data class LexicalConceptNode(
        @field:Element
        var definition: DefinitionNode? = null
    )

    @Root(name = "definition", strict = false)
    @Namespace(prefix = "wn")
    data class DefinitionNode(
        @field:Element
        @Namespace(prefix = "rdf")
        var value: String = ""
    )
}
