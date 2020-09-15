package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.db.category.CategoryDao
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfigDao
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfigWithRelations
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewDao
import com.github.cheapmon.balalaika.data.mappers.CategoryEntityToDataCategory
import com.github.cheapmon.balalaika.data.mappers.DictionaryViewWithCategoriesToDictionaryView
import com.github.cheapmon.balalaika.data.prefs.PreferenceStorage
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryView
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Suppress("EXPERIMENTAL_API_USAGE")
@Singleton
class ConfigRepository @Inject internal constructor(
    private val storage: PreferenceStorage,
    private val configDao: DictionaryConfigDao,
    private val categoryDao: CategoryDao,
    private val dictionaryViewDao: DictionaryViewDao,
    private val toDataCategory: CategoryEntityToDataCategory,
    private val toDictionaryView: DictionaryViewWithCategoriesToDictionaryView
) {
    private fun getConfig(): Flow<DictionaryConfigWithRelations?> =
        storage.openDictionary
            .flatMapLatest { it?.let { configDao.getConfigFor(it) } ?: flowOf(null) }

    fun getSortCategory(): Flow<DataCategory?> =
        getConfig().map { config -> config?.category?.let { toDataCategory(it) } }

    fun getSortCategories(): Flow<List<DataCategory>?> =
        storage.openDictionary
            .flatMapLatest { id -> id?.let { categoryDao.getSortable(it) } ?: flowOf(null) }
            .map { list -> list?.map { toDataCategory(it) } }

    fun getDictionaryView(): Flow<DictionaryView?> =
        getConfig().map { config -> config?.view?.let { toDictionaryView(it) } }

    fun getDictionaryViews(): Flow<List<DictionaryView>?> =
        storage.openDictionary
            .flatMapLatest { id -> id?.let { dictionaryViewDao.getAll(it) } ?: flowOf(null) }
            .map { list -> list?.map { toDictionaryView(it) } }
}
