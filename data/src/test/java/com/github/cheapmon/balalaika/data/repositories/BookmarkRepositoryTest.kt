package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.db.FakeBookmarkDao
import com.github.cheapmon.balalaika.data.db.FakeDictionaryEntryDao
import com.github.cheapmon.balalaika.data.db.FakeDictionaryViewDao
import com.github.cheapmon.balalaika.data.mappers.DictionaryEntryEntityToDictionaryEntryMapper
import com.github.cheapmon.balalaika.model.Dictionary
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.InstalledDictionary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
internal class BookmarkRepositoryTest {
    private val dispatcher = TestCoroutineDispatcher()

    private val bookmarkDao = FakeBookmarkDao()
    private val viewDao = FakeDictionaryViewDao()
    private val entryDao = FakeDictionaryEntryDao()
    private val toDictionaryEntry = mock(DictionaryEntryEntityToDictionaryEntryMapper::class.java)

    private val repository = BookmarkRepository(
        bookmarkDao,
        viewDao,
        entryDao,
        toDictionaryEntry
    )

    private val dictionary = InstalledDictionary(
        dictionary = Dictionary(
            id = "dic_a",
            version = 0,
            name = "Dictionary A",
            summary = "",
            authors = "",
            additionalInfo = ""
        )
    )

    @Test
    fun getBookmarkedEntries() = dispatcher.runBlockingTest {
        assertEquals(
            emptyList<DictionaryEntry>(),
            repository.getBookmarkedEntries(dictionary).first()
        )
    }
}
