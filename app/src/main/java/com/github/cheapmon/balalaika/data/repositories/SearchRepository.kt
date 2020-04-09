package com.github.cheapmon.balalaika.data.repositories

import androidx.paging.DataSource
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.di.ActivityScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ActivityScope
@Suppress("EXPERIMENTAL_API_USAGE")
class SearchRepository @Inject constructor(
    private val dictionaryEntryDao: DictionaryEntryDao
) {
    private val queryChannel: ConflatedBroadcastChannel<String> = ConflatedBroadcastChannel()
    private val restrictionChannel: ConflatedBroadcastChannel<SearchRestriction> =
        ConflatedBroadcastChannel()
    private val inProgressChannel: ConflatedBroadcastChannel<Boolean> =
        ConflatedBroadcastChannel(false)

    init {
        restrictionChannel.offer(SearchRestriction.None)
    }

    val lexemes = queryChannel.asFlow().distinctUntilChanged().debounce(300)
        .combine(restrictionChannel.asFlow().distinctUntilChanged()) { q, r ->
            when (r) {
                is SearchRestriction.None ->
                    dictionaryEntryDao.findLexemes(q)
                is SearchRestriction.Some ->
                    dictionaryEntryDao.findLexemesWith(q, r.category.categoryId, r.restriction)
            }
        }
    val query = queryChannel.asFlow()
    val restriction = restrictionChannel.asFlow()
    val inProgress = inProgressChannel.asFlow()

    fun setQuery(query: String) {
        queryChannel.offer(query)
    }

    fun setRestriction(restriction: SearchRestriction) {
        restrictionChannel.offer(restriction)
    }

    suspend fun getDictionaryEntriesFor(lexemeId: Long): List<DictionaryEntry> {
        inProgressChannel.offer(true)
        val q = queryChannel.asFlow().first()
        val r = restrictionChannel.asFlow().first()
        val result = when(r) {
            is SearchRestriction.None ->
                dictionaryEntryDao.find(q, lexemeId)
            is SearchRestriction.Some ->
                dictionaryEntryDao.findWith(q, r.category.categoryId, r.restriction, lexemeId)
        }
        inProgressChannel.offer(false)
        return result
    }

    fun clearRestriction() {
        restrictionChannel.offer(SearchRestriction.None)
    }
}