package com.github.cheapmon.balalaika.data.db

import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class FakeDictionaryDao : DictionaryDao {
    private val dictionaries = mutableListOf<DictionaryEntity>()

    override fun getAll(): Flow<List<DictionaryEntity>> {
        return flowOf(dictionaries)
    }

    override fun findById(id: String): Flow<DictionaryEntity?> {
        return flowOf(dictionaries.find { it.id == id })
    }

    override suspend fun insertAll(dictionary: List<DictionaryEntity>) {
        dictionaries.addAll(dictionary)
    }

    override suspend fun remove(id: String) {
        dictionaries.removeAll { it.id == id }
    }

    override suspend fun clear() = dictionaries.clear()

    override suspend fun count(): Int = dictionaries.size
}