package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetType
import com.github.cheapmon.balalaika.data.db.history.HistoryItemEntity
import com.github.cheapmon.balalaika.data.db.history.HistoryItemWithCategory
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.HistoryItem
import com.github.cheapmon.balalaika.model.SearchRestriction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class HistoryItemWithCategoryToHistoryItemTest {
    @Test
    fun mapsToHistoryItem() = runBlockingTest {
        val from = HistoryItemWithCategory(
            historyItem = HistoryItemEntity(
                id = 1,
                categoryId = "cat_a",
                dictionaryId = "dic_a",
                restriction = "restriction",
                query = "query"
            ),
            category = CategoryEntity(
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
        val to = HistoryItem(
            id = 1,
            query = "query",
            restriction = SearchRestriction(
                category = DataCategory(
                    id = "cat_a",
                    name = "Category A",
                    iconName = "ic_circle",
                    sequence = 0
                ),
                text = "restriction"
            )
        )
        val result = HistoryItemWithCategoryToHistoryItem(
            CategoryEntityToDataCategory()
        ).invoke(from)
        Assert.assertEquals(to, result)
    }
}
