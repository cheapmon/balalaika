package com.github.cheapmon.balalaika

import com.github.cheapmon.balalaika.db.Category
import com.github.cheapmon.balalaika.db.Word
import com.github.cheapmon.balalaika.util.CSV
import com.github.cheapmon.balalaika.util.ResourceLoader
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class CsvTest {

    private object MockResourceLoader : ResourceLoader {
        override val defaultCategoriesID: Int = 0
        override val defaultWordsID: Int = 1
        override fun openCSV(resourceID: Int): InputStream {
            return when (resourceID) {
                this.defaultCategoriesID -> ByteArrayInputStream("""
                    id,name,widget
                    title,Title,plain
                    wordnet,Wordnet,url
                """.trimIndent().toByteArray())
                this.defaultWordsID -> ByteArrayInputStream("""
                        id
                        word1
                        word2
                        word3
                    """.trimIndent().toByteArray())
                else -> ByteArrayInputStream("".toByteArray())
            }
        }
    }

    private val csv = CSV(MockResourceLoader)

    @Test
    fun `Correctly parses categories`() {
        assertArrayEquals(csv.getCategories(), arrayOf(
                Category(id = 0, externalId = "title", name = "Title", widget = "plain"),
                Category(id = 0, externalId = "wordnet", name = "Wordnet", widget = "url")
        ))
    }

    @Test
    fun `Correctly parses words`() {
        assertArrayEquals(csv.getWords(), arrayOf(
                Word(id = 0, externalId = "word1"),
                Word(id = 0, externalId = "word2"),
                Word(id = 0, externalId = "word3")
        ))
    }

}
