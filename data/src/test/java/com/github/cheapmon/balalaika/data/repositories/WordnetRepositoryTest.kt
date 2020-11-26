package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.mappers.RDFNodeToWordnetInfo
import com.github.cheapmon.balalaika.data.repositories.wordnet.RDFNode
import com.github.cheapmon.balalaika.data.repositories.wordnet.WordnetApi
import com.github.cheapmon.balalaika.data.result.LoadState
import com.github.cheapmon.balalaika.data.result.Result
import com.github.cheapmon.balalaika.model.WordnetInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
internal class WordnetRepositoryTest {
    private val wordnetApi = mock(WordnetApi::class.java)
    private val repository = WordnetRepository(
        wordnetApi = wordnetApi,
        toWordnetInfo = RDFNodeToWordnetInfo()
    )

    private val node = RDFNode(
        lexicalEntryList = listOf(
            RDFNode.LexicalEntryNode(
                canonicalForm = RDFNode.CanonicalFormNode(
                    writtenRep = "petitioner"
                ),
                partOfSpeech = RDFNode.PartOfSpeechNode(
                    resource = "&wn;noun"
                )
            ),
            RDFNode.LexicalEntryNode(
                canonicalForm = RDFNode.CanonicalFormNode(
                    writtenRep = "requester"
                ),
                partOfSpeech = RDFNode.PartOfSpeechNode(
                    resource = "&wn;noun"
                )
            ),
            RDFNode.LexicalEntryNode(
                canonicalForm = RDFNode.CanonicalFormNode(
                    writtenRep = "suer"
                ),
                partOfSpeech = RDFNode.PartOfSpeechNode(
                    resource = "&wn;noun"
                )
            ),
            RDFNode.LexicalEntryNode(
                canonicalForm = RDFNode.CanonicalFormNode(
                    writtenRep = "suppliant"
                ),
                partOfSpeech = RDFNode.PartOfSpeechNode(
                    resource = "&wn;noun"
                )
            ),
            RDFNode.LexicalEntryNode(
                canonicalForm = RDFNode.CanonicalFormNode(
                    writtenRep = "supplicant"
                ),
                partOfSpeech = RDFNode.PartOfSpeechNode(
                    resource = "&wn;noun"
                )
            )
        ), lexicalConceptList = listOf(
            RDFNode.LexicalConceptNode(
                definition = RDFNode.DefinitionNode(
                    value = "one praying humbly for something; &quot;a suppliant for her favors&quot;"
                )
            ),
            RDFNode.LexicalConceptNode(
                definition = RDFNode.DefinitionNode(
                    value = "someone who petitions a court for redress of a grievance or recovery of a right"
                )
            )
        )
    )

    @Test
    fun loadsWordnetData(): Unit = runBlockingTest {
        `when`(wordnetApi.getWordnetData("test")).thenReturn(node)
        val result = repository.getWordnetData("test").toList()
        Assert.assertTrue(result[0] is LoadState.Init)
        Assert.assertTrue(result[1] is LoadState.Loading)
        val loadState = result.last()
        Assert.assertTrue(loadState is LoadState.Finished && loadState.data is Result.Success)
        Assert.assertEquals(
            WordnetInfo(
                entries = listOf(
                    WordnetInfo.LexicalEntry(
                        representation = "petitioner",
                        partOfSpeech = "Noun"
                    ), WordnetInfo.LexicalEntry(
                        representation = "requester",
                        partOfSpeech = "Noun"
                    ), WordnetInfo.LexicalEntry(
                        representation = "suer",
                        partOfSpeech = "Noun"
                    ), WordnetInfo.LexicalEntry(
                        representation = "suppliant",
                        partOfSpeech = "Noun"
                    ), WordnetInfo.LexicalEntry(
                        representation = "supplicant",
                        partOfSpeech = "Noun"
                    )
                ),
                definitions = listOf(
                    WordnetInfo.Definition(
                        explanation = "one praying humbly for something",
                        examples = listOf(
                            "a suppliant for her favors"
                        )
                    ),
                    WordnetInfo.Definition(
                        explanation = "someone who petitions a court for redress of a grievance or recovery of a right",
                        examples = emptyList()
                    )
                )
            ),
            ((loadState as LoadState.Finished).data as Result.Success).data
        )
    }
}