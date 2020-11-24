package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetType
import com.github.cheapmon.balalaika.model.DataCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class CategoryEntityToDataCategoryTest {
    @Test
    fun mapsToDataCategory() = runBlockingTest {
        val from = CategoryEntity(
            id = "cat_a",
            dictionaryId = "dic_a",
            name = "Category A",
            widget = WidgetType.PLAIN,
            iconName = "ic_circle",
            sequence = 0,
            hidden = false,
            sortable = false
        )
        val to = DataCategory(
            id = "cat_a",
            name = "Category A",
            iconName = "ic_circle",
            sequence = 0
        )
        val result = CategoryEntityToDataCategory().invoke(from)
        Assert.assertEquals(to, result)
    }
}
