package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.entities.*
import com.github.cheapmon.balalaika.util.ComparatorUtil
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Suppress("EXPERIMENTAL_API_USAGE")
class DictionaryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val lexemeDao: LexemeDao,
    private val propertyDao: PropertyDao,
    private val dictionaryDao: DictionaryViewDao
) {
    private val orderingChannel: ConflatedBroadcastChannel<Comparator<DictionaryEntry>?> =
        ConflatedBroadcastChannel()
    private val dictionaryViewIdChannel: ConflatedBroadcastChannel<Long?> =
        ConflatedBroadcastChannel(null)
    private val inProgressChannel: ConflatedBroadcastChannel<Boolean> =
        ConflatedBroadcastChannel(true)

    val lexemes = propertyDao.count()
        .flatMapLatest { dictionaryViewIdChannel.asFlow().distinctUntilChanged() }
        .flatMapLatest {
            if (it != null) dictionaryDao.findCategoriesById(it) else flowOf(null)
        }.flatMapLatest {
            if (it == null) propertyDao.getAllVisible() else propertyDao.getAllFiltered(it)
        }.combine(orderingChannel.asFlow().distinctUntilChanged()) { props, order ->
            inProgressChannel.offer(true)
            val entries = props.groupBy { it.lexeme }.toEntries()
            val result = if (order == null) entries else entries.sortedWith(order)
            inProgressChannel.offer(false)
            result
        }
    val dictionaryViews = dictionaryDao.getAllWithCategories()
    val bookmarks = lexemeDao.getBookmarks()
    val comparators = ComparatorUtil.comparators
    val inProgress = inProgressChannel.asFlow()

    fun setOrdering(comparatorName: String) {
        val key = if (ComparatorUtil.comparators.containsKey(comparatorName)) comparatorName
        else ComparatorUtil.DEFAULT_KEY
        orderingChannel.offer(ComparatorUtil.comparators[key])
    }

    fun setDictionaryView(dictionaryViewId: Long) {
        dictionaryViewIdChannel.offer(dictionaryViewId)
    }

    suspend fun addComparators() {
        categoryDao.getAll().first().forEach {
            if (it.orderBy) ComparatorUtil.addPropertyComparator(it.name, it.categoryId)
        }
    }

    suspend fun toggleBookmark(lexemeId: Long) {
        lexemeDao.toggleBookmark(lexemeId)
    }

    suspend fun clearBookmarks() {
        lexemeDao.clearBookmarks()
    }

    private suspend fun Map<Lexeme, List<PropertyWithRelations>>.toEntries(): List<DictionaryEntry> {
        return this.map { (lexeme, props) ->
            val baseId = lexeme.baseId
            val base = if (baseId != null) lexemeDao.findById(baseId).first() else null
            DictionaryEntry(lexeme, base, props)
        }
    }
}