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
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
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
internal class DictionaryEntityTest {
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

    @Before
    fun createDb(): Unit = runBlockingTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        db.dictionaries().insertAll(listOf(dictionaryA, dictionaryB))
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun getDictionaries(): Unit = runBlockingTest {
        assertEquals(
            2,
            db.dictionaries().count()
        )
    }

    @Test
    fun findDictionary(): Unit = runBlockingTest {
        assertEquals(
            dictionaryA,
            db.dictionaries().findById("dic_a").first()
        )
        assertNull(db.dictionaries().findById("garbage").first())
    }

    @Test
    fun insertDictionary(): Unit = runBlockingTest {
        val dictionaryC = DictionaryEntity(
            id = "dic_c",
            version = 0,
            name = "Dictionary C",
            summary = "",
            authors = "",
            additionalInfo = ""
        )
        db.dictionaries().insertAll(listOf(dictionaryC))
        assertEquals(
            3,
            db.dictionaries().count()
        )
        assertEquals(
            listOf(dictionaryA, dictionaryB, dictionaryC),
            db.dictionaries().getAll().first()
        )
    }

    @Test
    fun replaceDictionaryOnInsert(): Unit = runBlockingTest {
        val dictionaryA = DictionaryEntity(
            id = "dic_a",
            version = 1,
            name = "Dictionary A",
            summary = "",
            authors = "",
            additionalInfo = "Version 1"
        )
        db.dictionaries().insertAll(listOf(dictionaryA))
        assertEquals(
            2,
            db.dictionaries().count()
        )
        assertEquals(
            listOf(dictionaryB, dictionaryA),
            db.dictionaries().getAll().first()
        )
    }

    @Test
    fun removeDictionary(): Unit = runBlockingTest {
        db.dictionaries().remove("dic_a")
        assertEquals(
            1,
            db.dictionaries().count()
        )
        assertEquals(
            listOf(dictionaryB),
            db.dictionaries().getAll().first()
        )
    }

    @Test
    fun clearDictionaries(): Unit = runBlockingTest {
        db.dictionaries().clear()
        assertEquals(
            0,
            db.dictionaries().count()
        )
    }
}
