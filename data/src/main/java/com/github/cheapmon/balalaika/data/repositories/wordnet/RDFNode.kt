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
package com.github.cheapmon.balalaika.data.repositories.wordnet

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/** XML representation of Wordnet data */
@Xml(name = "rdf:RDF")
internal data class RDFNode(
    /** @suppress */
    @Element val lexicalEntryList: List<LexicalEntryNode>?,
    /** @suppress */
    @Element val lexicalConceptList: List<LexicalConceptNode>?
) {
    /** @suppress */
    @Xml(name = "ontolex:LexicalEntry")
    data class LexicalEntryNode(
        @Element val canonicalForm: CanonicalFormNode,
        @Element val partOfSpeech: PartOfSpeechNode
    )

    /** @suppress */
    @Xml(name = "ontolex:canonicalForm")
    data class CanonicalFormNode(
        @PropertyElement(name = "ontolex:writtenRep") val writtenRep: String
    )

    /** @suppress */
    @Xml(name = "wn:partOfSpeech")
    data class PartOfSpeechNode(
        @Attribute(name = "rdf:resource") val resource: String
    )

    /** @suppress */
    @Xml(name = "ontolex:LexicalConcept")
    data class LexicalConceptNode(
        @Element(name = "wn:definition") val definition: DefinitionNode
    )

    /** @suppress */
    @Xml(name = "wn:definition")
    data class DefinitionNode(
        @PropertyElement(name = "rdf:value") val value: String
    )
}
