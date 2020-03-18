package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.entities.Lexeme
import com.github.cheapmon.balalaika.data.entities.LexemeDao
import com.github.cheapmon.balalaika.data.entities.PropertyDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class SearchRepository(
    private val lexemeDao: LexemeDao,
    private val propertyDao: PropertyDao
) {
    private val queryChannel: ConflatedBroadcastChannel<String> = ConflatedBroadcastChannel()
    private val restrictionChannel: ConflatedBroadcastChannel<Pair<Long?, String?>> =
        ConflatedBroadcastChannel()

    init {
        restrictionChannel.offer(null to null)
    }

    val lexemes = queryChannel.asFlow().distinctUntilChanged().debounce(300)
        .combine(restrictionChannel.asFlow().distinctUntilChanged()) { q, (id, restriction) ->
            if (id != null && restriction != null)
                findLexemesMatchingRestricted(q, id, restriction).first()
            else findLexemesMatching(q).first()
        }

    fun setQuery(query: String) {
        queryChannel.offer(query)
    }

    fun setRestriction(categoryId: Long, restriction: String) {
        restrictionChannel.offer(categoryId to restriction)
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