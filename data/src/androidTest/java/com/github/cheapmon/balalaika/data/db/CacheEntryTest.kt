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
import com.github.cheapmon.balalaika.data.db.cache.CacheEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class CacheEntryTest {
    private lateinit var db: CacheDatabase

    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    private val entryA = CacheEntry(0, "lex_a")
    private val entryB = CacheEntry(1, "lex_b")
    private val entryC = CacheEntry(2, "lex_c")
    private val entryD = CacheEntry(3, "lex_d")

    @Before
    fun createDb(): Unit = runBlockingTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CacheDatabase::class.java).build()
        db.cacheEntries().insertAll(listOf(entryA, entryB, entryC, entryD))
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun getEntries(): Unit = runBlockingTest {
        assertEquals(
            4,
            db.cacheEntries().count()
        )
        assertEquals(
            listOf(entryA, entryB, entryC, entryD),
            db.cacheEntries().getAll()
        )
    }

    @Test
    fun getPage(): Unit = runBlockingTest {
        assertEquals(
            listOf(entryA, entryB).map { it.lexemeId },
            db.cacheEntries().getPage(2, 0)
        )
        assertEquals(
            listOf(entryC, entryD).map { it.lexemeId },
            db.cacheEntries().getPage(2, 2)
        )
    }

    @Test
    fun insertEntry(): Unit = runBlockingTest {
        val entryE = CacheEntry(4, "lex_e")
        db.cacheEntries().insertAll(listOf(entryE))
        assertEquals(
            5,
            db.cacheEntries().count()
        )
        assertEquals(
            listOf(entryA, entryB, entryC, entryD, entryE),
            db.cacheEntries().getAll()
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnId(): Unit = runBlockingTest {
        val faultyEntry = CacheEntry(0, "lex_e")
        db.cacheEntries().insertAll(listOf(faultyEntry))
    }

    @Test
    fun clearEntries(): Unit = runBlockingTest {
        db.cacheEntries().clear()
        assertEquals(
            emptyList<CacheEntry>(),
            db.cacheEntries().getAll()
        )
    }
}
