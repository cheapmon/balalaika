package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.di.ActivityScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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

    val entries = queryChannel.asFlow().distinctUntilChanged().debounce(300)
        .combine(restrictionChannel.asFlow().distinctUntilChanged()) { q, r ->
            inProgressChannel.offer(true)
            val result = when (r) {
                is SearchRestriction.None ->
                    dictionaryEntryDao.find(q)
                is SearchRestriction.Some ->
                    dictionaryEntryDao.findWith(q, r.category.categoryId, r.restriction)
            }
            inProgressChannel.offer(false)
            result
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

    fun clearRestriction() {
        restrictionChannel.offer(SearchRestriction.None)
    }
}