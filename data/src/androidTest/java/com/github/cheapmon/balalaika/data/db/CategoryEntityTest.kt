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
internal class CategoryEntityTest {
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
        dictionaryId = dictionaryA.id,
        name = "Category B",
        widget = WidgetType.PLAIN,
        iconName = "ic_circle",
        sequence = 0,
        hidden = false,
        sortable = true
    )

    @Before
    fun createDb(): Unit = runBlockingTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        db.dictionaries().insertAll(listOf(dictionaryA))
        db.categories().insertAll(listOf(categoryA, categoryB))
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun getCategories(): Unit = runBlockingTest {
        assertEquals(
            2,
            db.categories().count()
        )
        assertEquals(
            listOf(categoryA, categoryB),
            db.categories().getAll()
        )
    }

    @Test
    fun getSortableCategories(): Unit = runBlockingTest {
        assertEquals(
            listOf(categoryB),
            db.categories().getSortable(dictionaryA.id).first()
        )
    }

    @Test
    fun insertCategory(): Unit = runBlockingTest {
        val categoryC = CategoryEntity(
            id = "cat_c",
            dictionaryId = dictionaryA.id,
            name = "Category C",
            widget = WidgetType.PLAIN,
            iconName = "ic_circle",
            sequence = 0,
            hidden = false,
            sortable = false
        )
        db.categories().insertAll(listOf(categoryC))
        assertEquals(
            3,
            db.categories().count()
        )
        assertEquals(
            listOf(categoryA, categoryB, categoryC),
            db.categories().getAll()
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnId(): Unit = runBlockingTest {
        val categoryC = CategoryEntity(
            id = "cat_a",
            dictionaryId = dictionaryA.id,
            name = "Category C",
            widget = WidgetType.PLAIN,
            iconName = "ic_circle",
            sequence = 0,
            hidden = false,
            sortable = false
        )
        db.categories().insertAll(listOf(categoryC))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnDictionaryId(): Unit = runBlockingTest {
        val categoryC = CategoryEntity(
            id = "cat_c",
            dictionaryId = "garbage",
            name = "Category C",
            widget = WidgetType.PLAIN,
            iconName = "ic_circle",
            sequence = 0,
            hidden = false,
            sortable = false
        )
        db.categories().insertAll(listOf(categoryC))
    }

    @Test
    fun removeCategories(): Unit = runBlockingTest {
        db.categories().removeInDictionary(dictionaryA.id)
        assertEquals(
            0,
            db.categories().count()
        )
        assertEquals(
            emptyList<CategoryEntity>(),
            db.categories().getAll()
        )
    }

    @Test
    fun findCategory(): Unit = runBlockingTest {
        assertEquals(
            categoryA,
            db.categories().findById(dictionaryA.id, categoryA.id)
        )
        assertNull(db.categories().findById(dictionaryA.id, "garbage"))
        assertNull(db.categories().findById("garbage", categoryA.id))
    }
}
