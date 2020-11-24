package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.history.HistoryItemEntity
import com.github.cheapmon.balalaika.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class HistoryItemToHistoryItemEntityTest {
    @Test
    fun mapsToHistoryItemEntity() = runBlockingTest {
        val item = HistoryItem(
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
        val dictionary = InstalledDictionary(
            dictionary = Dictionary(
                id = "dic_a",
                version = 0,
                name = "Dictionary A",
                summary = "Summary",
                authors = "Authors",
                additionalInfo = "Additional info"
            ),
            isOpened = false
        )
        val expected = HistoryItemEntity(
            id = 1,
            categoryId = "cat_a",
            dictionaryId = "dic_a",
            restriction = "restriction",
            query = "query"
        )
        val result = HistoryItemToHistoryItemEntity().invoke(item, dictionary)
        Assert.assertEquals(expected, result)
    }
}
