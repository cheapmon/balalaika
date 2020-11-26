package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.db.FakeBookmarkDao
import com.github.cheapmon.balalaika.data.db.FakeDictionaryEntryDao
import com.github.cheapmon.balalaika.data.db.FakeDictionaryViewDao
import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkEntity
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.mappers.FakeDictionaryEntryEntityToDictionaryEntryMapper
import com.github.cheapmon.balalaika.data.util.Constants
import com.github.cheapmon.balalaika.model.Dictionary
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.InstalledDictionary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
internal class BookmarkRepositoryTest {
    private val dispatcher = TestCoroutineDispatcher()

    private val bookmarkDao = FakeBookmarkDao()
    private val viewDao = FakeDictionaryViewDao()
    private val entryDao = FakeDictionaryEntryDao()
    private val toDictionaryEntry = FakeDictionaryEntryEntityToDictionaryEntryMapper()

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
    private val bookmark = BookmarkEntity(2, "dic_a", "lex_b")
    private val view = DictionaryViewEntity(
        id = Constants.DEFAULT_DICTIONARY_VIEW_ID,
        dictionaryId = "dic_a",
        name = "All"
    )
    private val entries = arrayOf(
        DictionaryEntryEntity(
            lexeme = LexemeEntity(
                id = "lex_a",
                dictionaryId = "dic_a",
                form = "Lexeme A",
                baseId = null
            ),
            base = null,
            properties = emptyList(),
            bookmark = BookmarkEntity(1, "dic_a", "lex_a")
        ),
        DictionaryEntryEntity(
            lexeme = LexemeEntity(
                id = "lex_b",
                dictionaryId = "dic_a",
                form = "Lexeme B",
                baseId = null
            ),
            base = null,
            properties = emptyList(),
            bookmark = BookmarkEntity(2, "dic_a", "lex_b")
        )
    )

    @After
    fun after() {
        bookmarkDao.clear()
        viewDao.clear()
        entryDao.clear()
    }

    @Test
    fun getBookmarkedEntries() = dispatcher.runBlockingTest {
        assertEquals(
            emptyList<DictionaryEntry>(),
            repository.getBookmarkedEntries(dictionary).first()
        )
        bookmarkDao.insert(bookmark)
        viewDao.insertViews(listOf(view))
        entryDao.insert(*entries)
        assertEquals(
            listOf(
                DictionaryEntry(
                    id = "lex_b",
                    representation = "Lexeme B",
                    base = null,
                    properties = TreeMap(),
                    bookmark = null
                )
            ),
            repository.getBookmarkedEntries(dictionary).first()
        )
    }

    @Test
    fun addBookmark() = dispatcher.runBlockingTest {
        viewDao.insertViews(listOf(view))
        entryDao.insert(*entries)
        repository.addBookmark(
            dictionary = dictionary,
            dictionaryEntry = DictionaryEntry(
                id = "lex_a",
                representation = "Lexeme A",
                base = null,
                properties = TreeMap(),
                bookmark = null
            )
        )
        assertEquals(
            listOf(
                DictionaryEntry(
                    id = "lex_a",
                    representation = "Lexeme A",
                    base = null,
                    properties = TreeMap(),
                    bookmark = null
                )
            ),
            repository.getBookmarkedEntries(dictionary).first()
        )
    }

    @Test
    fun removeBookmark() = dispatcher.runBlockingTest {
        bookmarkDao.insert(bookmark)
        viewDao.insertViews(listOf(view))
        entryDao.insert(*entries)
        repository.removeBookmark(
            dictionary = dictionary,
            dictionaryEntry = DictionaryEntry(
                id = "lex_b",
                representation = "Lexeme B",
                base = null,
                properties = TreeMap(),
                bookmark = null
            )
        )
        assertEquals(
            emptyList<DictionaryEntry>(),
            repository.getBookmarkedEntries(dictionary).first()
        )
    }

    @Test
    fun clearBookmarks() = dispatcher.runBlockingTest {
        bookmarkDao.insert(bookmark)
        viewDao.insertViews(listOf(view))
        entryDao.insert(*entries)
        repository.clearBookmarks(dictionary)
        assertEquals(
            emptyList<DictionaryEntry>(),
            repository.getBookmarkedEntries(dictionary).first()
        )
    }
}
