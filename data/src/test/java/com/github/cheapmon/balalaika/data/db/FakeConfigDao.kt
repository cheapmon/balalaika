package com.github.cheapmon.balalaika.data.db

import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetType
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfig
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfigDao
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfigWithRelations
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class FakeConfigDao : DictionaryConfigDao {
    private val configs = mutableMapOf<String, DictionaryConfigWithRelations>()

    override fun getConfigFor(dictionaryId: String): Flow<DictionaryConfigWithRelations?> {
        return flow {
            emit(configs[dictionaryId])
        }
    }

    override suspend fun insert(config: DictionaryConfig) {
        val category = CategoryEntity(
            id = config.sortBy,
            dictionaryId = config.id,
            name = "",
            widget = WidgetType.PLAIN,
            iconName = "",
            sequence = 0,
            hidden = false,
            sortable = false
        )
        configs[config.id] = DictionaryConfigWithRelations(
            config = DictionaryConfig(
                id = config.id,
                sortBy = config.sortBy,
                filterBy = config.filterBy
            ),
            category = category,
            view = DictionaryViewWithCategories(
                dictionaryView = DictionaryViewEntity(
                    id = config.filterBy,
                    dictionaryId = config.id,
                    name = ""
                ),
                categories = listOf(category)
            )
        )
    }

    override suspend fun update(config: DictionaryConfig) = insert(config)

    override suspend fun removeConfigFor(dictionaryId: String) {
        configs.remove(dictionaryId)
    }

    internal fun clear() = configs.clear()
}
