package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkDao
import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkEntity
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewDao
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.data.mappers.DictionaryEntryEntityToDictionaryEntryMapper
import com.github.cheapmon.balalaika.data.util.Constants
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.InstalledDictionary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class BookmarkRepository @Inject internal constructor(
    private val bookmarkDao: BookmarkDao,
    private val viewDao: DictionaryViewDao,
    private val dictionaryEntryDao: DictionaryEntryDao,
    private val toDictionaryEntry: DictionaryEntryEntityToDictionaryEntryMapper
) {
    private suspend fun getDefaultDictionaryView(dictionary: InstalledDictionary): DictionaryViewWithCategories? =
        viewDao.findById(dictionary.id, Constants.DEFAULT_DICTIONARY_VIEW_ID)

    public suspend fun getBookmarkedEntries(
        dictionary: InstalledDictionary
    ): Flow<List<DictionaryEntry>> =
        bookmarkDao.getAll(dictionary.id).map { list ->
            list.mapNotNull { dictionaryEntryDao.findEntryById(dictionary.id, it.lexemeId) }
                .mapNotNull { entry ->
                    getDefaultDictionaryView(dictionary)?.let { view ->
                        toDictionaryEntry(entry, view)
                    }
                }
        }

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
