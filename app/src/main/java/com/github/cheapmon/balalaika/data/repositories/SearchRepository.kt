package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.entities.Lexeme
import com.github.cheapmon.balalaika.data.entities.LexemeDao
import com.github.cheapmon.balalaika.data.entities.PropertyDao
import com.github.cheapmon.balalaika.data.entities.SearchRestriction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class SearchRepository private constructor(
    private val lexemeDao: LexemeDao,
    private val propertyDao: PropertyDao
) {
    private val queryChannel: ConflatedBroadcastChannel<String> = ConflatedBroadcastChannel()
    private val restrictionChannel: ConflatedBroadcastChannel<SearchRestriction> =
        ConflatedBroadcastChannel()

    init {
        restrictionChannel.offer(SearchRestriction.None)
    }

    val lexemes = queryChannel.asFlow().distinctUntilChanged().debounce(300)
        .combine(restrictionChannel.asFlow().distinctUntilChanged()) { q, restriction ->
            when (restriction) {
                is SearchRestriction.None -> findLexemesMatching(q).first()
                is SearchRestriction.Some ->
                    findLexemesMatchingRestricted(
                        q,
                        restriction.category.categoryId,
                        restriction.restriction
                    ).first()
            }
        }.map { it.sortedBy { lexeme -> lexeme.form } }
    val query = queryChannel.asFlow()
    val restriction = restrictionChannel.asFlow()

    fun setQuery(query: String) {
        queryChannel.offer(query)
    }

    fun setRestriction(restriction: SearchRestriction) {
        restrictionChannel.offer(restriction)
    }

    fun clearRestriction() {
        restrictionChannel.offer(SearchRestriction.None)
    }

    private fun findLexemesMatching(query: String): Flow<List<Lexeme>> {
        return lexemeDao.findByForm(query).combine(propertyDao.findByValue(query)) { l, p ->
            (l + p.map { it.lexeme }).distinct().sortedBy { it.form }
        }
    }

    private fun findLexemesMatchingRestricted(
        query: String,
        categoryId: Long,
        restriction: String
    ): Flow<List<Lexeme>> {
        return propertyDao.findByValueRestricted(query, categoryId, restriction).map {
            it.map { prop -> prop.lexeme }.distinct()
        }
    }

    companion object {
        @Volatile
        private var instance: SearchRepository? = null

        fun getInstance(
            lexemeDao: LexemeDao,
            propertyDao: PropertyDao
        ): SearchRepository {
            return instance ?: synchronized(this) {
                instance ?: SearchRepository(
                    lexemeDao,
                    propertyDao
                ).also { instance = it }
            }
        }
    }
}