package com.github.cheapmon.balalaika

import com.github.cheapmon.balalaika.db.Category
import com.github.cheapmon.balalaika.db.Lemma
import com.github.cheapmon.balalaika.db.LemmaValue
import com.github.cheapmon.balalaika.db.Lexeme
import com.github.cheapmon.balalaika.util.CSV
import com.github.cheapmon.balalaika.util.ResourceLoader
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class CsvTest {

    private object MockResourceLoader : ResourceLoader {
        override val defaultCategoriesID: Int = 0
        override val defaultLemmataID: Int = 1
        override val defaultLexemesID: Int = 2
        override val defaultVersionID: Int = 3
        override fun openCSV(resourceID: Int): InputStream {
            return when (resourceID) {
                this.defaultCategoriesID -> ByteArrayInputStream("""
                    id,name,widget
                    title,Title,plain
                    wordnet,Wordnet,url
                """.trimIndent().toByteArray())
                this.defaultLemmataID -> ByteArrayInputStream("""
                    id,title,wordnet
                    word1,WORD,-
                    word2,WORD,-
                    word3,WORD,-
                    """.trimIndent().toByteArray())
                this.defaultLexemesID -> ByteArrayInputStream("""
                    lemma,lexeme
                    word1,word11
                    word2,word21
                    word3,word31
                """.trimIndent().toByteArray())
                this.defaultVersionID -> ByteArrayInputStream("""
                    key,value
                    version,3
                """.trimIndent().toByteArray())
                else -> ByteArrayInputStream("".toByteArray())
            }
        }
    }

    private val csv = CSV(MockResourceLoader)

    @Test
    fun `Correctly parses categories`() {
        assertArrayEquals(csv.getCategories(), arrayOf(
                Category(id = "title", name = "Title", widget = "plain"),
                Category(id = "wordnet", name = "Wordnet", widget = "url")
        ))
    }

    @Test
    fun `Correctly parses lemmata`() {
        assertArrayEquals(csv.getLemmata(), arrayOf(
                Lemma(id = "word1"),
                Lemma(id = "word2"),
                Lemma(id = "word3")
        ))
    }

    @Test
    fun `Correctly parses lemma values`() {
        assertArrayEquals(csv.getLemmaValues(), arrayOf(
                LemmaValue(lemmaId = "word1", categoryId = "title", value = "WORD"),
                LemmaValue(lemmaId = "word1", categoryId = "wordnet", value = "-"),
                LemmaValue(lemmaId = "word2", categoryId = "title", value = "WORD"),
                LemmaValue(lemmaId = "word2", categoryId = "wordnet", value = "-"),
                LemmaValue(lemmaId = "word3", categoryId = "title", value = "WORD"),
                LemmaValue(lemmaId = "word3", categoryId = "wordnet", value = "-")
        ))
    }

    @Test
    fun `Correctly parses lexemes`() {
        assertArrayEquals(csv.getLexemes(), arrayOf(
                Lexeme(lemmaId = "word1", lexeme = "word11"),
                Lexeme(lemmaId = "word2", lexeme = "word21"),
                Lexeme(lemmaId = "word3", lexeme = "word31")
        ))
    }

    @Test
    fun `Correctly parses version`() {
        assertEquals(csv.getVersion(), 3)
    }

}
