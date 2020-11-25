package com.github.cheapmon.balalaika.data.repositories.dictionary

import com.github.cheapmon.balalaika.model.Dictionary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
internal class RemoteDictionaryDataSourceTest {
    private val dispatcher = TestCoroutineDispatcher()
    private val mockApi: DictionaryApi = mock(DictionaryApi::class.java)
    private val dataSource = RemoteDictionaryDataSource(
        dispatcher,
        mockApi
    )

    @After
    fun after() {
        reset(mockApi)
    }

    @Test
    fun hasDictionaryList() = dispatcher.runBlockingTest {
        `when`(mockApi.getDictionaryList()).thenReturn(
            listOf(
                DictionaryJson(
                    id = "dic_a",
                    version = 0,
                    name = "Dictionary A",
                    summary = "Summary",
                    authors = "Authors",
                    additionalInfo = "Additional info"
                )
            )
        )
        val expected = listOf(
            Dictionary(
                id = "dic_a",
                version = 0,
                name = "Dictionary A",
                summary = "Summary",
                authors = "Authors",
                additionalInfo = "Additional info"
            )
        )
        Assert.assertEquals(
            expected,
            dataSource.getDictionaryList()
        )
    }

    @Test
    fun hasDictionary() = dispatcher.runBlockingTest {
        `when`(mockApi.hasDictionary("dic_a", 0)).thenReturn(true)
        `when`(mockApi.hasDictionary("dic_a", 1)).thenReturn(false)
        `when`(mockApi.hasDictionary("dic_b", 0)).thenReturn(false)
        `when`(mockApi.hasDictionary("dic_b", 1)).thenReturn(false)
        Assert.assertTrue(dataSource.hasDictionary("dic_a", 0))
        Assert.assertFalse(dataSource.hasDictionary("dic_a", 1))
        Assert.assertFalse(dataSource.hasDictionary("dic_b", 0))
        Assert.assertFalse(dataSource.hasDictionary("dic_b", 1))
    }

    @Test
    fun hasDictionaryContents() = dispatcher.runBlockingTest {
        `when`(mockApi.getDictionaryContents("dic_a", 0))
            .thenReturn(ByteArray(3).toResponseBody())
        Assert.assertEquals(3, dataSource.getDictionaryContents("dic_a", 0).size)
    }
}
