package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.db.FakeCategoryDao
import com.github.cheapmon.balalaika.data.db.FakeConfigDao
import com.github.cheapmon.balalaika.data.db.FakeDictionaryViewDao
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetType
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewToCategory
import com.github.cheapmon.balalaika.data.mappers.CategoryEntityToDataCategory
import com.github.cheapmon.balalaika.data.mappers.DictionaryViewWithCategoriesToDictionaryView
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.Dictionary
import com.github.cheapmon.balalaika.model.DictionaryView
import com.github.cheapmon.balalaika.model.InstalledDictionary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ConfigRepositoryTest {
    private val dispatcher = TestCoroutineDispatcher()

    private val configDao = FakeConfigDao()
    private val categoryDao = FakeCategoryDao()
    private val viewDao = FakeDictionaryViewDao()

    private val toDataCategory = CategoryEntityToDataCategory()
    private val toDictionaryView = DictionaryViewWithCategoriesToDictionaryView(toDataCategory)

    private val repository = DefaultConfigRepository(
        configDao,
        categoryDao,
        viewDao,
        toDataCategory,
        toDictionaryView
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
    private val category = DataCategory(
        id = "cat_a",
        name = "Category A",
        iconName = "ic_circle",
        sequence = 0
    )
    private val view = DictionaryView(
        id = "view_a",
        name = "View A",
        categories = listOf(category)
    )


    @After
    fun after() {
        configDao.clear()
        categoryDao.clear()
        viewDao.clear()
    }

    @Test
    fun getConfig() = dispatcher.runBlockingTest {
        Assert.assertNull(repository.getSortCategory(dictionary).flowOn(dispatcher).first())
        Assert.assertNull(repository.getDictionaryView(dictionary).flowOn(dispatcher).first())
        repository.updateConfig(dictionary, sortBy = category, filterBy = view)
        Assert.assertEquals(
            category.id,
            repository.getSortCategory(dictionary).flowOn(dispatcher).first()?.id
        )
        Assert.assertEquals(
            view.id,
            repository.getDictionaryView(dictionary).flowOn(dispatcher).first()?.id
        )
    }

    @Test
    fun getSortCategories() = dispatcher.runBlockingTest {
        categoryDao.insertAll(
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
                    sortable = true
                )
            )
        )
        Assert.assertEquals(
            listOf(
                DataCategory(
                    id = "cat_b",
                    name = "Category B",
                    iconName = "ic_circle",
                    sequence = 0
                )
            ),
            repository.getSortCategories(dictionary).flowOn(dispatcher).first()
        )
    }

    @Test
    fun getDictionaryViews() = dispatcher.runBlockingTest {
        viewDao.insertViews(
            listOf(
                DictionaryViewEntity(
                    id = "view_a",
                    dictionaryId = "dic_a",
                    name = "View A"
                ), DictionaryViewEntity(
                    id = "view_b",
                    dictionaryId = "dic_a",
                    name = "View B"
                )
            )
        )
        viewDao.insertRelation(
            listOf(
                DictionaryViewToCategory("view_a", "cat_a", "dic_a"),
                DictionaryViewToCategory("view_a", "cat_b", "dic_a"),
                DictionaryViewToCategory("view_b", "cat_a", "dic_a")
            )
        )
        Assert.assertEquals(
            listOf(
                "view_a" to listOf("cat_a", "cat_b"),
                "view_b" to listOf("cat_a")
            ),
            repository.getDictionaryViews(dictionary).flowOn(dispatcher).first()?.map { view ->
                view.id to view.categories.map { it.id }
            }
        )
    }
}
