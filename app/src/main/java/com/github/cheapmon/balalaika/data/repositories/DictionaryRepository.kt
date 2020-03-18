package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.entities.*
import com.github.cheapmon.balalaika.data.util.ComparatorMap
import com.github.cheapmon.balalaika.data.util.ComparatorUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class DictionaryRepository private constructor(
    private val categoryDao: CategoryDao,
    private val lexemeDao: LexemeDao,
    private val propertyDao: PropertyDao,
    private val dictionaryDao: DictionaryViewDao
) {
    private val orderingChannel: ConflatedBroadcastChannel<Comparator<DictionaryEntry>?> =
        ConflatedBroadcastChannel()
    private val categoryIdsChannel: ConflatedBroadcastChannel<List<Long>?> =
        ConflatedBroadcastChannel()
    private val comparatorsChannel: ConflatedBroadcastChannel<ComparatorMap> =
        ConflatedBroadcastChannel()

    init {
        orderingChannel.offer(null)
        categoryIdsChannel.offer(null)
        comparatorsChannel.offer(ComparatorUtil.comparators)
    }

    val lexemes = categoryIdsChannel.asFlow().distinctUntilChanged()
        .flatMapLatest {
            if (it == null) propertyDao.getAllVisible() else propertyDao.getAllFiltered(it)
        }.combine(orderingChannel.asFlow().distinctUntilChanged()) { props, order ->
            val result = props.groupBy { it.lexeme }.toEntries()
            if (order == null) result
            else result.sortedWith(order)
        }

    val comparators = comparatorsChannel.asFlow()

    val dictionaryViews = dictionaryDao.getAll()

    fun setOrdering(comparatorName: String) {
        val key = if (ComparatorUtil.comparators.containsKey(comparatorName)) comparatorName
        else ComparatorUtil.DEFAULT_KEY
        orderingChannel.offer(ComparatorUtil.comparators[key])
    }

    suspend fun setDictionaryView(dictionaryViewId: Long) {
        dictionaryDao.findByIdWithCategories(dictionaryViewId).first()
            ?.categories?.map { it.categoryId }
            .also { categoryIdsChannel.offer(it) }
    }

    suspend fun addComparators() {
        categoryDao.getAll().first().forEach {
            ComparatorUtil.addPropertyComparator(it.name, it.categoryId)
        }
        comparatorsChannel.offer(ComparatorUtil.comparators)
    }

    private suspend fun Map<Lexeme, List<PropertyWithRelations>>.toEntries(): List<DictionaryEntry> {
        return this.map { (lexeme, props) ->
            val baseId = lexeme.baseId
            val base = if (baseId != null) lexemeDao.findById(baseId).first() else null
            DictionaryEntry(lexeme, base, props)
        }
    }

    companion object {
        @Volatile
        private var instance: DictionaryRepository? = null

        fun getInstance(
            categoryDao: CategoryDao,
            lexemeDao: LexemeDao,
            propertyDao: PropertyDao,
            dictionaryViewDao: DictionaryViewDao
        ): DictionaryRepository {
            return instance ?: synchronized(this) {
                instance ?: DictionaryRepository(
                    categoryDao,
                    lexemeDao,
                    propertyDao,
                    dictionaryViewDao
                ).also { instance = it }
            }
        }
    }
}