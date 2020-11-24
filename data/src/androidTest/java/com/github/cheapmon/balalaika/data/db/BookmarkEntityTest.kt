/*
 * Copyright 2020 Simon Kaleschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cheapmon.balalaika.data.db

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkEntity
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class BookmarkEntityTest {
    private lateinit var db: AppDatabase

    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    private val dictionaryA = DictionaryEntity(
        id = "dic_a",
        version = 0,
        name = "Dictionary A",
        summary = "",
        authors = "",
        additionalInfo = ""
    )

    private val dictionaryB = DictionaryEntity(
        id = "dic_b",
        version = 0,
        name = "Dictionary B",
        summary = "",
        authors = "",
        additionalInfo = ""
    )

    private val lexemeA = LexemeEntity(
        id = "lex_a",
        dictionaryId = dictionaryA.id,
        form = "Lexeme A",
        baseId = null
    )

    private val lexemeB = LexemeEntity(
        id = "lex_b",
        dictionaryId = dictionaryB.id,
        form = "Lexeme B",
        baseId = null
    )

    private val bookmarkA = BookmarkEntity(
        id = 1,
        dictionaryId = dictionaryA.id,
        lexemeId = lexemeA.id
    )

    @Before
    fun createDb(): Unit = runBlockingTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        db.dictionaries().insertAll(listOf(dictionaryA, dictionaryB))
        db.lexemes().insertAll(listOf(lexemeA, lexemeB))
        db.bookmarks().insert(bookmarkA)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun getBookmarks(): Unit = runBlockingTest {
        assertEquals(
            1,
            db.bookmarks().count()
        )
        assertEquals(
            listOf(bookmarkA),
            db.bookmarks().getAll(dictionaryA.id).first()
        )
        assertEquals(
            emptyList<BookmarkEntity>(),
            db.bookmarks().getAll(dictionaryB.id).first()
        )
    }

    @Test
    fun findDictionary(): Unit = runBlockingTest {
        assertEquals(
            bookmarkA,
            db.bookmarks().findById(dictionaryA.id, lexemeA.id)
        )
        assertNull(db.bookmarks().findById(dictionaryA.id, lexemeB.id))
        assertNull(db.bookmarks().findById(dictionaryB.id, lexemeA.id))
        assertNull(db.bookmarks().findById(dictionaryB.id, lexemeB.id))
        assertNull(db.bookmarks().findById("garbage", lexemeA.id))
        assertNull(db.bookmarks().findById("garbage", "garbage"))
    }

    @Test
    fun insertBookmark(): Unit = runBlockingTest {
        val bookmarkB = BookmarkEntity(
            id = 2,
            dictionaryId = dictionaryB.id,
            lexemeId = lexemeB.id
        )
        db.bookmarks().insert(bookmarkB)
        assertEquals(
            2,
            db.bookmarks().count()
        )
        assertEquals(
            listOf(bookmarkB),
            db.bookmarks().getAll(dictionaryB.id).first()
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnId(): Unit = runBlockingTest {
        val faultyBookmark = BookmarkEntity(
            id = 1,
            dictionaryId = dictionaryA.id,
            lexemeId = lexemeA.id
        )
        db.bookmarks().insert(faultyBookmark)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnDictionaryId(): Unit = runBlockingTest {
        val faultyBookmark = BookmarkEntity(
            id = 1,
            dictionaryId = "garbage",
            lexemeId = lexemeA.id
        )
        db.bookmarks().insert(faultyBookmark)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnLexemeId(): Unit = runBlockingTest {
        val faultyBookmark = BookmarkEntity(
            id = 1,
            dictionaryId = dictionaryA.id,
            lexemeId = "garbage"
        )
        db.bookmarks().insert(faultyBookmark)
    }

    @Test
    fun removeBookmark(): Unit = runBlockingTest {
        db.bookmarks().remove(dictionaryA.id, lexemeA.id)
        assertEquals(
            emptyList<BookmarkEntity>(),
            db.bookmarks().getAll(dictionaryA.id).first()
        )
    }

    @Test
    fun removeBookmarksInDictionary(): Unit = runBlockingTest {
        db.bookmarks().removeInDictionary(dictionaryA.id)
        assertEquals(
            emptyList<BookmarkEntity>(),
            db.bookmarks().getAll(dictionaryA.id).first()
        )
    }

    @Test
    fun removeLexeme(): Unit = runBlockingTest {
        db.lexemes().removeInDictionary(dictionaryA.id)
        assertEquals(
            emptyList<BookmarkEntity>(),
            db.bookmarks().getAll(dictionaryA.id).first()
        )
    }

    @Test
    fun removeDictionary(): Unit = runBlockingTest {
        db.lexemes().removeInDictionary(dictionaryA.id)
        db.dictionaries().remove(dictionaryA.id)
        assertEquals(
            emptyList<BookmarkEntity>(),
            db.bookmarks().getAll(dictionaryA.id).first()
        )
    }
}
