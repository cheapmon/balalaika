package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.db.FakeHistoryItemDao
import com.github.cheapmon.balalaika.data.db.history.HistoryItemEntity
import com.github.cheapmon.balalaika.data.mappers.CategoryEntityToDataCategory
import com.github.cheapmon.balalaika.data.mappers.HistoryItemToHistoryItemEntity
import com.github.cheapmon.balalaika.data.mappers.HistoryItemWithCategoryToHistoryItem
import com.github.cheapmon.balalaika.model.Dictionary
import com.github.cheapmon.balalaika.model.HistoryItem
import com.github.cheapmon.balalaika.model.InstalledDictionary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class HistoryRepositoryTest {
    private val dao = FakeHistoryItemDao()
    private val repository = HistoryRepository(
        dao = dao,
        toHistoryItem = HistoryItemWithCategoryToHistoryItem(CategoryEntityToDataCategory()),
        fromHistoryItem = HistoryItemToHistoryItemEntity()
    )

    val dictionary = InstalledDictionary(
        dictionary = Dictionary(
            id = "dic_a",
            version = 0,
            name = "Dictionary A",
            summary = "",
            authors = "",
            additionalInfo = ""
        )
    )
    private val entities = listOf(
        HistoryItemEntity(1, null, "dic_a", null, "query"),
        HistoryItemEntity(2, null, "dic_a", null, "query"),
        HistoryItemEntity(3, null, "dic_a", null, "something")
    )
    private val items = listOf(
        HistoryItem(1, "query", null),
        HistoryItem(2, "query", null),
        HistoryItem(3, "something", null)
    )

    @After
    fun after() {
        dao.clear()
    }

    @Test
    fun isInitiallyEmpty() = runBlockingTest {
        Assert.assertTrue(repository.getHistoryItems(dictionary).first().isEmpty())
    }

    @Test
    fun hasHistoryItems() = runBlockingTest {
        for (entity in entities) dao.insert(entity)
        Assert.assertEquals(
            items,
            repository.getHistoryItems(dictionary).first()
        )
    }

    @Test
    fun addToHistory() = runBlockingTest {
        for (item in items) repository.addToHistory(dictionary, item)
        Assert.assertEquals(
            items.subList(1, 3),
            repository.getHistoryItems(dictionary).first()
        )
    }

    @Test
    fun removeFromHistory() = runBlockingTest {
        for (item in items) repository.addToHistory(dictionary, item)
        repository.removeFromHistory(dictionary, items.first())
        Assert.assertEquals(
            items.subList(1, 3),
            repository.getHistoryItems(dictionary).first()
        )
    }

    @Test
    fun clearHistory() = runBlockingTest {
        for (item in items) repository.addToHistory(dictionary, item)
        repository.clearHistory(dictionary)
        Assert.assertTrue(repository.getHistoryItems(dictionary).first().isEmpty())
    }
}
