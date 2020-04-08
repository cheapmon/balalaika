package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.entities.*
import com.github.cheapmon.balalaika.di.ActivityScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ActivityScope
@Suppress("EXPERIMENTAL_API_USAGE")
class SearchRepository @Inject constructor(
    private val lexemeDao: LexemeDao,
    private val propertyDao: PropertyDao
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
        .combine(restrictionChannel.asFlow().distinctUntilChanged()) { q, restriction ->
            inProgressChannel.offer(true)
            when (restriction) {
                is SearchRestriction.None -> findLexemesMatching(q).first()
                is SearchRestriction.Some ->
                    findLexemesMatchingRestricted(
                        q,
                        restriction.category.categoryId,
                        restriction.restriction
                    ).first()
            }
        }.map {
            val result = it.groupBy { prop -> prop.lexeme }
                .toEntries()
                .sortedBy { (lexeme, _) -> lexeme.form }
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

    private fun findLexemesMatching(query: String): Flow<List<PropertyWithRelations>> {
        return lexemeDao.findByForm(query).combine(propertyDao.findByValue(query)) { l, p ->
            (l.chunked(50).flatMap { lexemes ->
                propertyDao.findByLexemeId(lexemes.map { it.lexemeId }).first()
            } + p).distinct()
        }
    }

    private fun findLexemesMatchingRestricted(
        query: String,
        categoryId: Long,
        restriction: String
    ): Flow<List<PropertyWithRelations>> {
        return propertyDao.findByValueRestricted(query, categoryId, restriction)
    }

    private suspend fun Map<Lexeme, List<PropertyWithRelations>>.toEntries(): List<DictionaryEntry> {
        return this.map { (lexeme, props) ->
            val baseId = lexeme.baseId
            val base = if (baseId != null) lexemeDao.findById(baseId).first() else null
            DictionaryEntry(lexeme, base, props)
        }
    }
}