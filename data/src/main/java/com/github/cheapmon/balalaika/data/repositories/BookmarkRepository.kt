package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkDao
import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkEntity
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.InstalledDictionary
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class BookmarkRepository @Inject internal constructor(
    private val bookmarkDao: BookmarkDao
) {
    public suspend fun addBookmark(
        dictionary: InstalledDictionary,
        dictionaryEntry: DictionaryEntry
    ) {
        val bookmark = BookmarkEntity(
            dictionaryId = dictionary.id,
            lexemeId = dictionaryEntry.id
        )
        bookmarkDao.insert(bookmark)
    }

    public suspend fun removeBookmark(
        dictionary: InstalledDictionary,
        dictionaryEntry: DictionaryEntry
    ) {
        bookmarkDao.remove(dictionary.id, dictionaryEntry.id)
    }

    public suspend fun clearBookmarks(dictionary: InstalledDictionary) {
        bookmarkDao.removeInDictionary(dictionary.id)
    }
}
