package com.github.cheapmon.balalaika.data.db

import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkDao
import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class FakeBookmarkDao : BookmarkDao {
    private val bookmarks = mutableListOf<BookmarkEntity>()

    override suspend fun findById(dictionaryId: String, lexemeId: String): BookmarkEntity? {
        return bookmarks.find { it.dictionaryId == dictionaryId && it.lexemeId == lexemeId }
    }

    override fun getAll(dictionaryId: String): Flow<List<BookmarkEntity>> {
        return flow { emit(bookmarks.filter { it.dictionaryId == dictionaryId }) }
    }

    override suspend fun insert(bookmark: BookmarkEntity) {
        bookmarks.add(bookmark)
    }

    override suspend fun remove(dictionaryId: String, lexemeId: String) {
        bookmarks.removeAll { it.dictionaryId == dictionaryId && it.lexemeId == lexemeId }
    }

    override suspend fun removeInDictionary(dictionaryId: String) {
        bookmarks.removeAll { it.dictionaryId == dictionaryId }
    }

    override suspend fun count(): Int = bookmarks.size

    internal fun clear() = bookmarks.clear()
}