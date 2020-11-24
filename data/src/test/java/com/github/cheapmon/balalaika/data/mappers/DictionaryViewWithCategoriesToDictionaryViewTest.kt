package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetType
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DictionaryViewWithCategoriesToDictionaryViewTest {
    @Test
    fun mapsToDictionaryView() = runBlockingTest {
        val from = DictionaryViewWithCategories(
            dictionaryView = DictionaryViewEntity(
                id = "view_a",
                dictionaryId = "dic_a",
                name = "View A"
            ),
            categories = listOf(
                CategoryEntity(
                    id = "cat_a",
                    dictionaryId = "dic_a",
                    name = "Category A",
                    widget = WidgetType.PLAIN,
                    iconName = "ic_circle",
                    sequence = 0,
                    hidden = false,
                    sortable = false
                )
            )
        )
        val to = DictionaryView(
            id = "view_a",
            name = "View A",
            categories = listOf(
                DataCategory(
                    id = "cat_a",
                    name = "Category A",
                    iconName = "ic_circle",
                    sequence = 0
                )
            )
        )
        val result = DictionaryViewWithCategoriesToDictionaryView(
            CategoryEntityToDataCategory()
        ).invoke(from)
        Assert.assertEquals(to, result)
    }
}
