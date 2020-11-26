package com.github.cheapmon.balalaika.data.db

import androidx.paging.PagingSource
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryEntity

internal class FakeDictionaryEntryDao : DictionaryEntryDao {
    private val entries = mutableListOf<DictionaryEntryEntity>()

    override suspend fun findEntryById(
        dictionaryId: String,
        lexemeId: String
    ): DictionaryEntryEntity? {
        return entries.find { it.lexeme.dictionaryId == dictionaryId && it.lexeme.id == lexemeId }
    }

    override suspend fun getLexemes(dictionaryId: String, dictionaryViewId: String): List<String> {
        return entries.map { it.lexeme.id }
    }

    override suspend fun getLexemes(
        dictionaryId: String,
        dictionaryViewId: String,
        categoryId: String
    ): List<String> {
        return entries.map { it.lexeme.id }
    }

    override fun findLexemes(
        dictionaryId: String,
        query: String
    ): PagingSource<Int, DictionaryEntryEntity> {
        return object : PagingSource<Int, DictionaryEntryEntity>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DictionaryEntryEntity> {
                val result = entries.filter { it.lexeme.form.contains(query) }
                return LoadResult.Page(result, null, null)
            }
        }
    }

    override fun findLexemes(
        dictionaryId: String,
        query: String,
        categoryId: String,
        restriction: String
    ): PagingSource<Int, DictionaryEntryEntity> = findLexemes(dictionaryId, query)

    internal fun insert(vararg entries: DictionaryEntryEntity) = this.entries.addAll(entries)
    internal fun clear() = entries.clear()
}
