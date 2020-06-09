/*
 * Copyright 2020 Simon Kaleschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.category.CategoryDao
import com.github.cheapmon.balalaika.data.entities.category.WidgetType
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.entities.lexeme.LexemeDao
import com.github.cheapmon.balalaika.data.entities.view.DictionaryView
import com.github.cheapmon.balalaika.data.entities.view.DictionaryViewDao
import com.github.cheapmon.balalaika.di.ActivityScope
import com.github.cheapmon.balalaika.ui.bookmarks.BookmarksFragment
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryFragment
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Application-wide dictionary data handling
 *
 * @see DictionaryFragment
 * @see BookmarksFragment
 */
@ActivityScope
@Suppress("EXPERIMENTAL_API_USAGE")
class DictionaryRepository @Inject constructor(
    categoryDao: CategoryDao,
    private val lexemeDao: LexemeDao,
    private val dictionaryEntryDao: DictionaryEntryDao,
    dictionaryDao: DictionaryViewDao
) {
    /** Current [dictionary view][DictionaryView] selected in the user interface */
    private val dictionaryViewIdChannel: ConflatedBroadcastChannel<Long?> =
        ConflatedBroadcastChannel(null)

    /** Current [ordering category][Category] selected in the user interface */
    private val categoryIdChannel: ConflatedBroadcastChannel<Long?> =
        ConflatedBroadcastChannel(null)

    /**
     * State of computation
     *
     * Used to indicate ongoing calculations in the user interface.
     *
     * Holds `true` whenever the [dictionary view][DictionaryView] or [ordering category][Category]
     * has been changed, while new [dictionary entries][DictionaryEntry] are calculated.
     * Holds `false` when the computation is finished.
     */
    private val inProgressChannel: ConflatedBroadcastChannel<Boolean> =
        ConflatedBroadcastChannel(true)

    /** Dummy [category][Category] used when no category has been selected in the user interface */
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

    /** Lexemes currently shown, depending on user input */
    val lexemes = dictionaryViewIdChannel.asFlow().distinctUntilChanged()
        .combine(categoryIdChannel.asFlow().distinctUntilChanged()) { d, c ->
            val dictionaryViewId = d ?: 1
            // Use default ordering when no category has been selected
            if (c == null) dictionaryEntryDao.getLexemesFiltered(dictionaryViewId)
            else dictionaryEntryDao.getLexemesSorted(dictionaryViewId, c)
        }

    /** Identifiers of [lexemes], used to identify where to scroll to */
    val positions = dictionaryViewIdChannel.asFlow().distinctUntilChanged()
        .combine(categoryIdChannel.asFlow().distinctUntilChanged()) { d, c ->
            val dictionaryViewId = d ?: 1
            if (c == null) dictionaryEntryDao.getIdsFiltered(dictionaryViewId)
            else dictionaryEntryDao.getIdsSorted(dictionaryViewId, c)
        }.flattenConcat()

    /** All available [dictionary views][DictionaryView] */
    val dictionaryViews = dictionaryDao.getAllWithCategories()

    /** All [lexemes][Lexeme] that are currently bookmarked */
    val bookmarks = lexemeDao.getBookmarks()

    /** All available sortable [categories][Category] */
    val categories = categoryDao.getSortable().map {
        listOf(defaultCategory) + it
    }

    /** Current state of computation */
    val inProgress = inProgressChannel.asFlow()

    /** Select [category][Category] to order [lexemes] by */
    fun setCategoryId(categoryId: Long) {
        if (categoryId == defaultCategory.categoryId) categoryIdChannel.offer(null)
        else categoryIdChannel.offer(categoryId)
    }

    /** Select a [view][DictionaryView] for the current dictionary */
    fun setDictionaryViewId(dictionaryViewId: Long) {
        dictionaryViewIdChannel.offer(dictionaryViewId)
    }

    /** Get all [dictionary entries][DictionaryEntry] for a [lexeme][Lexeme] */
    suspend fun getDictionaryEntriesFor(lexemeId: Long): List<DictionaryEntry> {
        inProgressChannel.offer(true)
        val d = dictionaryViewIdChannel.asFlow().first() ?: 1
        val c = categoryIdChannel.asFlow().first()
        val result = if (c == null) dictionaryEntryDao.getFiltered(d, lexemeId)
        else dictionaryEntryDao.getSorted(d, c, lexemeId)
        inProgressChannel.offer(false)
        return result
    }

    /** Toggle bookmark state for a [lexeme][Lexeme] */
    suspend fun toggleBookmark(lexemeId: Long) {
        lexemeDao.toggleBookmark(lexemeId)
    }

    /** Remove all bookmarks */
    suspend fun clearBookmarks() {
        lexemeDao.clearBookmarks()
    }
}