package com.github.cheapmon.balalaika

import com.github.cheapmon.balalaika.db.Category
import com.github.cheapmon.balalaika.db.Lexeme
import com.github.cheapmon.balalaika.db.LexemeProperty
import com.github.cheapmon.balalaika.db.FullForm
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
        override val defaultLexemesID: Int = 1
        override val defaultFullFormsID: Int = 2
        override val defaultVersionID: Int = 3
        override val defaultPropertiesID: Int = 4
        override fun openCSV(resourceID: Int): InputStream {
            return when (resourceID) {
                this.defaultCategoriesID -> ByteArrayInputStream("""
                    id,name,widget
                    title,Title,plain
                    wordnet,Wordnet,url
                """.trimIndent().toByteArray())
                this.defaultLexemesID -> ByteArrayInputStream("""
                    id,title,wordnet
                    word1,WORD,-
                    word2,WORD,-
                    word3,WORD,-
                    """.trimIndent().toByteArray())
                this.defaultFullFormsID -> ByteArrayInputStream("""
                    lexeme,full_form
                    word1,word11
                    word2,word21
                    word3,word31
                """.trimIndent().toByteArray())
                this.defaultVersionID -> ByteArrayInputStream("""
                    key,value
                    version,3
                """.trimIndent().toByteArray())
                this.defaultPropertiesID -> ByteArrayInputStream("""
                    lexeme,category,value
                    word1,wordnet,https://example.org
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
    fun `Correctly parses lexemes`() {
        assertArrayEquals(csv.getLexemes(), arrayOf(
                Lexeme(id = "word1"),
                Lexeme(id = "word2"),
                Lexeme(id = "word3")
        ))
    }

    @Test
    fun `Correctly parses lexeme properties`() {
        assertArrayEquals(csv.getLexemeProperties(), arrayOf(
                LexemeProperty(id = 0, lexemeId = "word1", categoryId = "title", value = "WORD"),
                LexemeProperty(id = 0, lexemeId = "word1", categoryId = "wordnet", value = "-"),
                LexemeProperty(id = 0, lexemeId = "word2", categoryId = "title", value = "WORD"),
                LexemeProperty(id = 0, lexemeId = "word2", categoryId = "wordnet", value = "-"),
                LexemeProperty(id = 0, lexemeId = "word3", categoryId = "title", value = "WORD"),
                LexemeProperty(id = 0, lexemeId = "word3", categoryId = "wordnet", value = "-"),
                LexemeProperty(id = 0, lexemeId = "word1", categoryId = "wordnet", value = "https://example.org")
        ))
    }

    @Test
    fun `Correctly parses full forms`() {
        assertArrayEquals(csv.getFullForms(), arrayOf(
                FullForm(id = 0, lexemeId = "word1", fullForm = "word11"),
                FullForm(id = 0, lexemeId = "word2", fullForm = "word21"),
                FullForm(id = 0, lexemeId = "word3", fullForm = "word31")
        ))
    }

    @Test
    fun `Correctly parses version`() {
        assertEquals(csv.getVersion(), 3)
    }

}
