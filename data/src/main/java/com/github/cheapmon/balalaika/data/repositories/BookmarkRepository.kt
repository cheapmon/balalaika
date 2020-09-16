package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkDao
import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkEntity
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.mappers.DictionaryEntryEntityToDictionaryEntry
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.InstalledDictionary
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
public class BookmarkRepository @Inject internal constructor(
    private val configRepository: ConfigRepository,
    private val bookmarkDao: BookmarkDao,
    private val dictionaryEntryDao: DictionaryEntryDao,
    private val toDictionaryEntry: DictionaryEntryEntityToDictionaryEntry
) {
    public suspend fun getBookmarkedEntries(
        dictionary: InstalledDictionary
    ): Flow<List<DictionaryEntry>> =
        bookmarkDao.getAll(dictionary.id).map { list ->
            list.mapNotNull { dictionaryEntryDao.findEntryById(dictionary.id, it.lexemeId) }
                .mapNotNull { entry ->
                    configRepository.getDefaultDictionaryView(dictionary)?.let { view ->
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
