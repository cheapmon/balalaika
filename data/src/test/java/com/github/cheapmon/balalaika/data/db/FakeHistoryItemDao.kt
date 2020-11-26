package com.github.cheapmon.balalaika.data.db

import com.github.cheapmon.balalaika.data.db.history.HistoryItemDao
import com.github.cheapmon.balalaika.data.db.history.HistoryItemEntity
import com.github.cheapmon.balalaika.data.db.history.HistoryItemWithCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class FakeHistoryItemDao : HistoryItemDao {
    private val items = mutableListOf<HistoryItemEntity>()

    private val mapper = { item: HistoryItemEntity ->
        HistoryItemWithCategory(
            historyItem = item,
            category = null
        )
    }

    override fun getAll(dictionaryId: String): Flow<List<HistoryItemWithCategory>> {
        val result = items
            .filter { it.dictionaryId == dictionaryId }
            .map(mapper)
        return flow { emit(result) }
    }

    override suspend fun getAll(): List<HistoryItemEntity> = items

    override suspend fun insert(historyItem: HistoryItemEntity) {
        items.add(historyItem)
    }

    override suspend fun removeSimilar(dictionaryId: String, query: String) {
        items.removeAll { it.dictionaryId == dictionaryId && it.query == query }
    }

    override suspend fun remove(historyEntry: HistoryItemEntity) {
        items.removeAll { it == historyEntry }
    }

    override suspend fun removeInDictionary(dictionaryId: String) {
        items.removeAll { it.dictionaryId == dictionaryId }
    }

    override suspend fun count(): Int = items.size

    internal fun clear() = items.clear()
}