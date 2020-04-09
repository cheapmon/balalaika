package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.category.CategoryDao
import com.github.cheapmon.balalaika.data.entities.category.WidgetType
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.entities.lexeme.LexemeDao
import com.github.cheapmon.balalaika.data.entities.view.DictionaryViewDao
import com.github.cheapmon.balalaika.di.ActivityScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ActivityScope
@Suppress("EXPERIMENTAL_API_USAGE")
class DictionaryRepository @Inject constructor(
    categoryDao: CategoryDao,
    private val lexemeDao: LexemeDao,
    private val dictionaryEntryDao: DictionaryEntryDao,
    dictionaryDao: DictionaryViewDao
) {
    private val dictionaryViewIdChannel: ConflatedBroadcastChannel<Long?> =
        ConflatedBroadcastChannel(null)
    private val categoryIdChannel: ConflatedBroadcastChannel<Long?> =
        ConflatedBroadcastChannel(null)
    private val inProgressChannel: ConflatedBroadcastChannel<Boolean> =
        ConflatedBroadcastChannel(true)

    private val defaultCategory = Category(
        -1,
        "default",
        "Default",
        WidgetType.PLAIN,
        "",
        -1,
        hidden = true,
        orderBy = false
    )

    val lexemes = dictionaryViewIdChannel.asFlow().distinctUntilChanged()
        .combine(categoryIdChannel.asFlow().distinctUntilChanged()) { d, c ->
            val dictionaryViewId = d ?: 1
            if (c == null) dictionaryEntryDao.getLexemesFiltered(dictionaryViewId)
            else dictionaryEntryDao.getLexemesSorted(dictionaryViewId, c)
        }
    val positions = dictionaryViewIdChannel.asFlow().distinctUntilChanged()
        .combine(categoryIdChannel.asFlow().distinctUntilChanged()) { d, c ->
            val dictionaryViewId = d ?: 1
            if (c == null) dictionaryEntryDao.getIdsFiltered(dictionaryViewId)
            else dictionaryEntryDao.getIdsSorted(dictionaryViewId, c)
        }.flattenConcat()
    val dictionaryViews = dictionaryDao.getAllWithCategories()
    val bookmarks = lexemeDao.getBookmarks()
    val categories = categoryDao.getSortable().map {
        listOf(defaultCategory) + it
    }
    val inProgress = inProgressChannel.asFlow()

    fun setCategoryId(categoryId: Long) {
        if (categoryId == defaultCategory.categoryId) categoryIdChannel.offer(categoryId)
        else categoryIdChannel.offer(null)
    }

    fun setDictionaryViewId(dictionaryViewId: Long) {
        dictionaryViewIdChannel.offer(dictionaryViewId)
    }

    suspend fun getDictionaryEntriesFor(lexemeId: Long): List<DictionaryEntry> {
        inProgressChannel.offer(true)
        val d = dictionaryViewIdChannel.asFlow().first() ?: 1
        val c = categoryIdChannel.asFlow().first()
        val result = if (c == null) dictionaryEntryDao.getFiltered(d, lexemeId)
        else dictionaryEntryDao.getSorted(d, c, lexemeId)
        inProgressChannel.offer(false)
        return result
    }

    suspend fun toggleBookmark(lexemeId: Long) {
        lexemeDao.toggleBookmark(lexemeId)
    }

    suspend fun clearBookmarks() {
        lexemeDao.clearBookmarks()
    }
}