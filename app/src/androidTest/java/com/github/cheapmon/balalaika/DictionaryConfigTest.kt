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
package com.github.cheapmon.balalaika

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.cheapmon.balalaika.db.AppDatabase
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.category.WidgetType
import com.github.cheapmon.balalaika.db.entities.config.DictionaryConfig
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.db.entities.view.DictionaryView
import com.github.cheapmon.balalaika.db.entities.view.DictionaryViewToCategory
import java.io.IOException
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DictionaryConfigTest {
    private lateinit var db: AppDatabase

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun createDb() = runBlockingTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        db.dictionaries().insertAll(listOf(Dictionary(id = "dic_a"), Dictionary(id = "dic_b")))
        db.categories().insertAll(
            listOf(
                Category(
                    id = "cat_a",
                    dictionaryId = "dic_a",
                    name = "Category A",
                    widget = WidgetType.PLAIN,
                    iconId = 0,
                    sequence = 0,
                    hidden = false,
                    orderBy = false
                ),
                Category(
                    id = "cat_b",
                    dictionaryId = "dic_a",
                    name = "Category B",
                    widget = WidgetType.PLAIN,
                    iconId = 0,
                    sequence = 0,
                    hidden = false,
                    orderBy = false
                ),
                Category(
                    id = "cat_c",
                    dictionaryId = "dic_b",
                    name = "Category C",
                    widget = WidgetType.PLAIN,
                    iconId = 0,
                    sequence = 0,
                    hidden = false,
                    orderBy = false
                )
            )
        )
        db.dictionaryViews().insertViews(
            listOf(
                DictionaryView("all", "dic_a", "All"),
                DictionaryView("none", "dic_b", "None")
            )
        )
        db.dictionaryViews().insertRelation(
            listOf(
                DictionaryViewToCategory("all", "cat_a", "dic_a"),
                DictionaryViewToCategory("all", "cat_b", "dic_a")
            )
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertConfig() = runBlockingTest {
        db.configurations().insert(
            DictionaryConfig(id = "dic_a", orderBy = "cat_a", filterBy = "all")
        )
        db.configurations().insert(
            DictionaryConfig(id = "dic_b", orderBy = "cat_c", filterBy = "none")
        )
    }

    @Test
    @Throws(Exception::class)
    fun replaceConfigOnInsert() = runBlockingTest {
        db.configurations().insert(
            DictionaryConfig(id = "dic_a", orderBy = "cat_a", filterBy = "all")
        )
        db.configurations().insert(
            DictionaryConfig(id = "dic_a", orderBy = "cat_b", filterBy = "all")
        )
        assertEquals(
            db.configurations().getConfigFor("dic_a"),
            DictionaryConfig(id = "dic_a", orderBy = "cat_b", filterBy = "all")
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    @Throws(Exception::class)
    fun useAppropriateForeignKeys() = runBlockingTest {
        db.configurations().insert(
            DictionaryConfig("dic_a", "cat_c", "none")
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    @Throws(Exception::class)
    fun enforceForeignKeys() = runBlockingTest {
        db.configurations().insert(DictionaryConfig("asdf", "jklö", "uiop"))
    }

    @Test
    @Throws(Exception::class)
    fun updateConfig() = runBlockingTest {
        db.configurations().insert(
            DictionaryConfig(id = "dic_a", orderBy = "cat_a", filterBy = "all")
        )
        db.configurations().update(
            DictionaryConfig(id = "dic_a", orderBy = "cat_b", filterBy = "all")
        )
        assertEquals(
            db.configurations().getConfigFor("dic_a"),
            DictionaryConfig(id = "dic_a", orderBy = "cat_b", filterBy = "all")
        )
    }

    @Test
    @Throws(Exception::class)
    fun getConfig() = runBlockingTest {
        db.configurations().insert(
            DictionaryConfig(id = "dic_a", orderBy = "cat_a", filterBy = "all")
        )
        assertEquals(
            db.configurations().getConfigFor("dic_a"),
            DictionaryConfig(id = "dic_a", orderBy = "cat_a", filterBy = "all")
        )
        assertNull(db.configurations().getConfigFor("garbage"))
    }

    @Test
    @Throws(Exception::class)
    fun removeConfig() = runBlockingTest {
        db.configurations().insert(
            DictionaryConfig(id = "dic_a", orderBy = "cat_a", filterBy = "all")
        )
        assertNotNull(db.configurations().getConfigFor("dic_a"))
        db.configurations().removeConfigFor("dic_a")
        assertNull(db.configurations().getConfigFor("dic_a"))
    }
}
