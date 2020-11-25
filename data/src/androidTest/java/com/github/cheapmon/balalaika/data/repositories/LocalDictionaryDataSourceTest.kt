package com.github.cheapmon.balalaika.data.repositories

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.cheapmon.balalaika.data.repositories.dictionary.LocalDictionaryDataSource
import com.github.cheapmon.balalaika.data.util.TestCoroutineDispatcherRule
import com.github.cheapmon.balalaika.model.Dictionary
import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileNotFoundException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class LocalDictionaryDataSourceTest {
    @get:Rule
    val coroutineRule = TestCoroutineDispatcherRule()

    private val dataSource: LocalDictionaryDataSource = LocalDictionaryDataSource(
        coroutineRule.dispatcher,
        ApplicationProvider.getApplicationContext(),
        Moshi.Builder().build()
    )

    @Test
    fun hasDictionaryList() = coroutineRule.dispatcher.runBlockingTest {
        val expected = listOf(
            Dictionary(
                name = "Sample dictionary",
                id = "sample",
                version = 1,
                authors = "Paul Hemetsberger",
                summary = "dict.cc is not only an online dictionary. It's an attempt to create a platform where users from all over the world can share their knowledge in the field of translations. Every visitor can suggest new translations and correct or confirm other users' suggestions. The challenging and most important part of the project is the so-called Contribute! system making this process possible. To guarantee that the users' work is not lost in case something happens to the maintainer of dict.cc (Paul Hemetsberger), the resulting vocabulary database can be, downloaded anytime.",
                additionalInfo = "https://www.dict.cc/?s=about%3A&l=e"
            )
        )
        Assert.assertEquals(
            expected,
            dataSource.getDictionaryList()
        )
    }

    @Test
    fun hasDictionary() = coroutineRule.dispatcher.runBlockingTest {
        Assert.assertTrue(dataSource.hasDictionary("sample", 1))
        Assert.assertFalse(dataSource.hasDictionary("sample", 0))
        Assert.assertFalse(dataSource.hasDictionary("sample", 2))
        Assert.assertFalse(dataSource.hasDictionary("simple", 1))
        Assert.assertFalse(dataSource.hasDictionary("simple", 0))
    }

    @Test
    fun hasDictionaryContents() = coroutineRule.dispatcher.runBlockingTest {
        Assert.assertEquals(
            2222,
            dataSource.getDictionaryContents("sample", 1).size
        )
    }

    @Test(expected = FileNotFoundException::class)
    fun failsOnDictionaryId() = coroutineRule.dispatcher.runBlockingTest {
        dataSource.getDictionaryContents("simple", 1)
    }

    @Test(expected = FileNotFoundException::class)
    fun failsOnVersion() = coroutineRule.dispatcher.runBlockingTest {
        dataSource.getDictionaryContents("sample", 0)
    }
}