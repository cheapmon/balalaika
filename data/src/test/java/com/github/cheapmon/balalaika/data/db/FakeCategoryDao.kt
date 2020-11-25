package com.github.cheapmon.balalaika.data.db

import com.github.cheapmon.balalaika.data.db.category.CategoryDao
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class FakeCategoryDao : CategoryDao {
    private val categories = mutableListOf<CategoryEntity>()

    override suspend fun getAll(): List<CategoryEntity> {
        return categories
    }

    override fun getSortable(dictionaryId: String): Flow<List<CategoryEntity>> {
        return flow {
            emit(categories.filter { it.sortable })
        }
    }

    override suspend fun insertAll(categories: List<CategoryEntity>) {
        this.categories.addAll(categories)
    }

    override suspend fun removeInDictionary(dictionaryId: String) {
        categories.removeAll { it.dictionaryId == dictionaryId }
    }

    override suspend fun findById(dictionaryId: String, id: String): CategoryEntity? {
        return categories.find { it.dictionaryId == dictionaryId && it.id == id }
    }

    override suspend fun count(): Int {
        return categories.size
    }

    internal fun clear() = categories.clear()
}