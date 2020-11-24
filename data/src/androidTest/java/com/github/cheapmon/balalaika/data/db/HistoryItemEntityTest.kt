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
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetType
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.history.HistoryItemEntity
import com.github.cheapmon.balalaika.data.db.history.HistoryItemWithCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class HistoryItemEntityTest {
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

    private val categoryA = CategoryEntity(
        id = "cat_a",
        dictionaryId = dictionaryA.id,
        name = "Category A",
        widget = WidgetType.PLAIN,
        iconName = "ic_circle",
        sequence = 0,
        hidden = false,
        sortable = false
    )

    private val categoryB = CategoryEntity(
        id = "cat_b",
        dictionaryId = dictionaryB.id,
        name = "Category B",
        widget = WidgetType.PLAIN,
        iconName = "ic_circle",
        sequence = 0,
        hidden = false,
        sortable = false
    )

    private val historyItemA = HistoryItemEntity(
        id = 1,
        categoryId = null,
        dictionaryId = dictionaryA.id,
        restriction = null,
        query = "Query A"
    )

    private val historyItemB = HistoryItemEntity(
        id = 2,
        categoryId = categoryB.id,
        dictionaryId = dictionaryB.id,
        restriction = "Restriction",
        query = "Query B"
    )

    @Before
    fun createDb(): Unit = runBlockingTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        db.dictionaries().insertAll(listOf(dictionaryA, dictionaryB))
        db.categories().insertAll(listOf(categoryA, categoryB))
        db.historyEntries().insert(historyItemA)
        db.historyEntries().insert(historyItemB)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun getHistoryItems(): Unit = runBlockingTest {
        assertEquals(
            2,
            db.historyEntries().count()
        )
        assertEquals(
            listOf(historyItemA, historyItemB),
            db.historyEntries().getAll()
        )
        assertEquals(
            listOf(HistoryItemWithCategory(historyItem = historyItemB, category = categoryB)),
            db.historyEntries().getAll(dictionaryB.id).first()
        )
    }

    @Test
    fun insertHistoryItem(): Unit = runBlockingTest {
        val historyItemC = HistoryItemEntity(
            id = 3,
            categoryId = null,
            dictionaryId = dictionaryA.id,
            restriction = null,
            query = "Query C"
        )
        db.historyEntries().insert(historyItemC)
        assertEquals(
            3,
            db.historyEntries().count()
        )
        assertEquals(
            listOf(historyItemA, historyItemB, historyItemC),
            db.historyEntries().getAll()
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnId(): Unit = runBlockingTest {
        val faultyHistoryItem = HistoryItemEntity(
            id = 1,
            categoryId = null,
            dictionaryId = dictionaryA.id,
            restriction = null,
            query = ""
        )
        db.historyEntries().insert(faultyHistoryItem)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnCategoryId(): Unit = runBlockingTest {
        val faultyHistoryItem = HistoryItemEntity(
            id = 3,
            categoryId = "garbage",
            dictionaryId = dictionaryA.id,
            restriction = null,
            query = ""
        )
        db.historyEntries().insert(faultyHistoryItem)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnDictionaryId(): Unit = runBlockingTest {
        val faultyHistoryItem = HistoryItemEntity(
            id = 3,
            categoryId = categoryA.id,
            dictionaryId = "garbage",
            restriction = null,
            query = ""
        )
        db.historyEntries().insert(faultyHistoryItem)
    }

    @Test
    fun removeHistoryItem(): Unit = runBlockingTest {
        db.historyEntries().remove(historyItemA)
        assertEquals(
            listOf(historyItemB),
            db.historyEntries().getAll()
        )
    }

    @Test
    fun removeHistoryItemsInDictionary(): Unit = runBlockingTest {
        db.historyEntries().removeInDictionary(dictionaryA.id)
        assertEquals(
            listOf(historyItemB),
            db.historyEntries().getAll()
        )
    }

    @Test
    fun removeSimilarHistoryItems(): Unit = runBlockingTest {
        db.historyEntries().removeSimilar(dictionaryA.id, "Query A")
        assertEquals(
            listOf(historyItemB),
            db.historyEntries().getAll()
        )
    }

    @Test
    fun removeCategory(): Unit = runBlockingTest {
        db.categories().removeInDictionary(dictionaryB.id)
        assertEquals(
            listOf(historyItemA),
            db.historyEntries().getAll()
        )
    }

    @Test
    fun removeDictionary(): Unit = runBlockingTest {
        db.categories().removeInDictionary(dictionaryB.id)
        db.dictionaries().remove(dictionaryB.id)
        assertEquals(
            listOf(historyItemA),
            db.historyEntries().getAll()
        )
    }
}
