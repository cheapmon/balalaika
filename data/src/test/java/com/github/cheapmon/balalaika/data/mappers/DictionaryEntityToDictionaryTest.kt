package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.model.Dictionary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DictionaryEntityToDictionaryTest {
    @Test
    fun mapsToDictionary() = runBlockingTest {
        val from = DictionaryEntity(
            id = "dic_a",
            version = 0,
            name = "Dictionary A",
            summary = "Summary",
            authors = "Authors",
            additionalInfo = "Additional info"
        )
        val to = Dictionary(
            id = "dic_a",
            version = 0,
            name = "Dictionary A",
            summary = "Summary",
            authors = "Authors",
            additionalInfo = "Additional info"
        )
        val result = DictionaryEntityToDictionary().invoke(from)
        Assert.assertEquals(to, result)
    }
}
