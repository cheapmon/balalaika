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
package com.github.cheapmon.balalaika

import com.github.cheapmon.balalaika.data.dictionary.wordnet.RDFNode
import com.github.cheapmon.balalaika.data.dictionary.wordnet.RDFNode.CanonicalFormNode
import com.github.cheapmon.balalaika.data.dictionary.wordnet.RDFNode.DefinitionNode
import com.github.cheapmon.balalaika.data.dictionary.wordnet.RDFNode.LexicalConceptNode
import com.github.cheapmon.balalaika.data.dictionary.wordnet.RDFNode.LexicalEntryNode
import com.github.cheapmon.balalaika.data.dictionary.wordnet.RDFNode.PartOfSpeechNode
import java.io.InputStream
import javax.xml.stream.XMLStreamException
import org.junit.Assert.assertEquals
import org.junit.Test
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.core.ValueRequiredException

class WordnetParserTest {
    private val serializer = Persister()

    @Test
    fun `parses partOfSpeech node`() {
        val node: PartOfSpeechNode = serializer.read(
            """<wn:partOfSpeech
                        xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                        xmlns:wn="http://wordnet-rdf.princeton.edu/ontology#"
                        rdf:resource="http://wordnet-rdf.princeton.edu/ontology#adjective"/>"""
        )
        assertEquals(
            PartOfSpeechNode(
                resource = "http://wordnet-rdf.princeton.edu/ontology#adjective"
            ),
            node
        )
    }

    @Test
    fun `parses canonicalForm node`() {
        val node: CanonicalFormNode = serializer.read(
            """<ontolex:canonicalForm
                        xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
                        xmlns:ontolex="http://www.w3.org/ns/lemon/ontolex#"
                        rdf:parseType="Resource">
                        <ontolex:writtenRep xml:lang="en">petitioner</ontolex:writtenRep>
                    </ontolex:canonicalForm>"""
        )
        assertEquals(
            CanonicalFormNode(
                writtenRep = "petitioner"
            ),
            node
        )
    }

    @Test
    fun `parses LexicalEntry node`() {
        val node: LexicalEntryNode = serializer.read(
            """<ontolex:LexicalEntry
                        xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
                        xmlns:ontolex="http://www.w3.org/ns/lemon/ontolex#"
                        xmlns:wn="http://wordnet-rdf.princeton.edu/ontology#"
                        rdf:about="#petitioner-n">
                        <ontolex:canonicalForm rdf:parseType="Resource">
                            <ontolex:writtenRep xml:lang="en">petitioner</ontolex:writtenRep>
                        </ontolex:canonicalForm>
                        <ontolex:sense rdf:resource="#petitioner-10439776-n"/>
                        <ontolex:sense rdf:resource="#petitioner-10691631-n"/>
                        <wn:partOfSpeech
                            rdf:resource="http://wordnet-rdf.princeton.edu/ontology#noun"/>
                    </ontolex:LexicalEntry>"""
        )
        assertEquals(
            LexicalEntryNode(
                CanonicalFormNode(
                    writtenRep = "petitioner"
                ),
                PartOfSpeechNode(
                    resource = "http://wordnet-rdf.princeton.edu/ontology#noun"
                )
            ),
            node
        )
    }

    @Test
    fun `parses definition node`() {
        val node: DefinitionNode = serializer.read(
            """<wn:definition 
                        xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
                        xmlns:ontolex="http://www.w3.org/ns/lemon/ontolex#"
                        xmlns:wn="http://wordnet-rdf.princeton.edu/ontology#"
                        rdf:parseType="Resource">
                        <rdf:value xml:lang="en">one praying humbly for something; "a suppliant for her favors"</rdf:value>
                    </wn:definition>"""
        )
        assertEquals(
            DefinitionNode(
                value = "one praying humbly for something; \"a suppliant for her favors\""
            ),
            node
        )
    }

    @Test
    fun `parses LexicalConcept node`() {
        val node: LexicalConceptNode = serializer.read(
            """<ontolex:LexicalConcept 
                        xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
                        xmlns:ontolex="http://www.w3.org/ns/lemon/ontolex#"
                        xmlns:wn="http://wordnet-rdf.princeton.edu/ontology#"
                        xmlns:owl="http://www.w3.org/2002/07/owl#"
                        xmlns:dc="http://purl.org/dc/terms/"
                        rdf:about="http://wordnet-rdf.princeton.edu/rdf/id/10439776-n">
                        <owl:sameAs rdf:resource="http://ili.globalwordnet.org/ili/i91977"/>
                        <wn:partOfSpeech rdf:resource="http://wordnet-rdf.princeton.edu/ontology#noun"/>
                        <dc:subject>noun.person</dc:subject>
                        <wn:definition rdf:parseType="Resource">
                            <rdf:value xml:lang="en">one praying humbly for something; "a suppliant for her favors"</rdf:value>
                        </wn:definition>
                        <wn:hypernym rdf:resource="http://wordnet-rdf.princeton.edu/rdf/id/09630310-n"/>
                        <wn:hyponym rdf:resource="http://wordnet-rdf.princeton.edu/rdf/id/09870495-n"/>
                        <wn:hyponym rdf:resource="http://wordnet-rdf.princeton.edu/rdf/id/10479448-n"/>
                        <wn:hyponym rdf:resource="http://wordnet-rdf.princeton.edu/rdf/id/10642716-n"/>
                    </ontolex:LexicalConcept>"""
        )
        assertEquals(
            LexicalConceptNode(
                definition = DefinitionNode(
                    value = "one praying humbly for something; \"a suppliant for her favors\""
                )
            ),
            node
        )
    }

    @Test
    fun `parses RDF node`() {
        val source = this::class.java.getResourceAsStream("petitioner.rdf")!!
        val node: RDFNode = serializer.read(source)
        assertEquals(
            RDFNode(
                lexicalEntryList = listOf(
                    LexicalEntryNode(
                        canonicalForm = CanonicalFormNode(
                            writtenRep = "petitioner"
                        ),
                        partOfSpeech = PartOfSpeechNode(
                            resource = "http://wordnet-rdf.princeton.edu/ontology#noun"
                        )
                    ),
                    LexicalEntryNode(
                        canonicalForm = CanonicalFormNode(
                            writtenRep = "requester"
                        ),
                        partOfSpeech = PartOfSpeechNode(
                            resource = "http://wordnet-rdf.princeton.edu/ontology#noun"
                        )
                    ),
                    LexicalEntryNode(
                        canonicalForm = CanonicalFormNode(
                            writtenRep = "suer"
                        ),
                        partOfSpeech = PartOfSpeechNode(
                            resource = "http://wordnet-rdf.princeton.edu/ontology#noun"
                        )
                    ),
                    LexicalEntryNode(
                        canonicalForm = CanonicalFormNode(
                            writtenRep = "suppliant"
                        ),
                        partOfSpeech = PartOfSpeechNode(
                            resource = "http://wordnet-rdf.princeton.edu/ontology#noun"
                        )
                    ),
                    LexicalEntryNode(
                        canonicalForm = CanonicalFormNode(
                            writtenRep = "supplicant"
                        ),
                        partOfSpeech = PartOfSpeechNode(
                            resource = "http://wordnet-rdf.princeton.edu/ontology#noun"
                        )
                    )
                ), lexicalConceptList = listOf(
                    LexicalConceptNode(
                        definition = DefinitionNode(
                            value = "one praying humbly for something; \"a suppliant for her favors\""
                        )
                    ),
                    LexicalConceptNode(
                        definition = DefinitionNode(
                            value = "someone who petitions a court for redress of a grievance or recovery of a right"
                        )
                    )
                )
            ),
            node
        )
    }

    @Test(expected = XMLStreamException::class)
    fun `does not parse when namespace is missing`() {
        serializer.read<RDFNode>("<rdf:RDF />")
    }

    @Test(expected = ValueRequiredException::class)
    fun `does not parse wrong node type`() {
        serializer.read<CanonicalFormNode>("""<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" />""")
    }

    private inline fun <reified T> Persister.read(source: String): T =
        this.read(T::class.java, source)

    private inline fun <reified T> Persister.read(source: InputStream): T =
        this.read(T::class.java, source)
}
