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
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfig
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewToCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class DictionaryConfigTest {
    private lateinit var db: AppDatabase

    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb(): Unit = runBlockingTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        db.dictionaries().insertAll(
            listOf(
                DictionaryEntity(
                    id = "dic_a",
                    version = 0,
                    name = "Dictionary A",
                    summary = "",
                    authors = "",
                    additionalInfo = ""
                ),
                DictionaryEntity(
                    id = "dic_b",
                    version = 0,
                    name = "Dictionary B",
                    summary = "",
                    authors = "",
                    additionalInfo = ""
                )
            )
        )
        db.categories().insertAll(
            listOf(
                CategoryEntity(
                    id = "cat_a",
                    dictionaryId = "dic_a",
                    name = "Category A",
                    widget = WidgetType.PLAIN,
                    iconName = "ic_circle",
                    sequence = 0,
                    hidden = false,
                    sortable = false
                ),
                CategoryEntity(
                    id = "cat_b",
                    dictionaryId = "dic_a",
                    name = "Category B",
                    widget = WidgetType.PLAIN,
                    iconName = "ic_circle",
                    sequence = 0,
                    hidden = false,
                    sortable = false
                ),
                CategoryEntity(
                    id = "cat_c",
                    dictionaryId = "dic_b",
                    name = "Category C",
                    widget = WidgetType.PLAIN,
                    iconName = "ic_circle",
                    sequence = 0,
                    hidden = false,
                    sortable = false
                )
            )
        )
        db.dictionaryViews().insertViews(
            listOf(
                DictionaryViewEntity("all", "dic_a", "All"),
                DictionaryViewEntity("none", "dic_b", "None")
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
    fun insertConfig(): Unit = runBlockingTest {
        db.configurations().insert(
            DictionaryConfig(id = "dic_a", sortBy = "cat_a", filterBy = "all")
        )
        db.configurations().insert(
            DictionaryConfig(id = "dic_b", sortBy = "cat_c", filterBy = "none")
        )
    }

    @Test
    fun replaceConfigOnInsert(): Unit = runBlockingTest {
        db.configurations().insert(
            DictionaryConfig(id = "dic_a", sortBy = "cat_a", filterBy = "all")
        )
        db.configurations().insert(
            DictionaryConfig(id = "dic_a", sortBy = "cat_b", filterBy = "all")
        )
        assertEquals(
            DictionaryConfig(id = "dic_a", sortBy = "cat_b", filterBy = "all"),
            db.configurations().getConfigFor("dic_a").first()?.config
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    fun useAppropriateForeignKeys(): Unit = runBlockingTest {
        db.configurations().insert(
            DictionaryConfig("dic_a", "cat_c", "none")
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    fun enforceForeignKeys(): Unit = runBlockingTest {
        db.configurations().insert(DictionaryConfig("asdf", "jklö", "uiop"))
    }

    @Test
    fun updateConfig(): Unit = runBlockingTest {
        db.configurations().insert(
            DictionaryConfig(id = "dic_a", sortBy = "cat_a", filterBy = "all")
        )
        db.configurations().update(
            DictionaryConfig(id = "dic_a", sortBy = "cat_b", filterBy = "all")
        )
        assertEquals(
            DictionaryConfig(id = "dic_a", sortBy = "cat_b", filterBy = "all"),
            db.configurations().getConfigFor("dic_a").first()?.config
        )
    }

    @Test
    fun getConfig(): Unit = runBlockingTest {
        db.configurations().insert(
            DictionaryConfig(id = "dic_a", sortBy = "cat_a", filterBy = "all")
        )
        assertEquals(
            DictionaryConfig(id = "dic_a", sortBy = "cat_a", filterBy = "all"),
            db.configurations().getConfigFor("dic_a").first()?.config
        )
        assertNull(db.configurations().getConfigFor("garbage").first())
    }

    @Test
    fun removeConfig(): Unit = runBlockingTest {
        db.configurations().insert(
            DictionaryConfig(id = "dic_a", sortBy = "cat_a", filterBy = "all")
        )
        assertNotNull(db.configurations().getConfigFor("dic_a").first())
        db.configurations().removeConfigFor("dic_a")
        assertNull(db.configurations().getConfigFor("dic_a").first())
    }
}
