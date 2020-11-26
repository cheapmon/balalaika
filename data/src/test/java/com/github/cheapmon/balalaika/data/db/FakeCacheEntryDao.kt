package com.github.cheapmon.balalaika.data.db

import com.github.cheapmon.balalaika.data.db.cache.CacheEntry
import com.github.cheapmon.balalaika.data.db.cache.CacheEntryDao

internal class FakeCacheEntryDao : CacheEntryDao {
    private val entries = mutableListOf<CacheEntry>()

    override suspend fun getAll(): List<CacheEntry> = entries

    override suspend fun getPage(count: Int, offset: Long): List<String> {
        val start = entries.indexOfFirst { it.id == offset }
        if (start == -1) return emptyList()
        val end = minOf(start + count, entries.size)
        return entries.subList(start, end).map { it.lexemeId }
    }

    override suspend fun findEntry(lexemeId: String): Long? {
        return entries.find { it.lexemeId == lexemeId }?.id
    }

    override suspend fun insertAll(entries: List<CacheEntry>) {
        this.entries.addAll(entries)
    }

    override suspend fun clear() = entries.clear()

    override suspend fun count(): Int = entries.size
}
