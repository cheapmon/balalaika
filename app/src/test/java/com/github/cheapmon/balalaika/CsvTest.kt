package com.github.cheapmon.balalaika

import com.github.cheapmon.balalaika.util.CSV
import com.github.cheapmon.balalaika.util.ResourceLoader
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class CsvTest {

    private object MockResourceLoader : ResourceLoader {
        override val defaultCategoriesID: Int = 0
        override fun open(resourceID: Int): InputStream {
            return ByteArrayInputStream("""
                id,name,widget
                title,Title,plain
                wordnet,Wordnet,url
            """.trimIndent().toByteArray())
        }
    }

    private val csv = CSV(MockResourceLoader)

    @Test
    fun `Correctly parses categories`() {
        assertArrayEquals(csv.getCategories(), arrayOf(Pair("Title", "plain"), Pair("Wordnet", "url")))
    }

}
