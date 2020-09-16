package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.db.category.CategoryDao
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfigDao
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfigWithRelations
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewDao
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.data.mappers.CategoryEntityToDataCategory
import com.github.cheapmon.balalaika.data.mappers.DictionaryViewWithCategoriesToDictionaryView
import com.github.cheapmon.balalaika.data.util.Constants
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.Dictionary
import com.github.cheapmon.balalaika.model.DictionaryView
import com.github.cheapmon.balalaika.model.InstalledDictionary
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Suppress("EXPERIMENTAL_API_USAGE")
@Singleton
public class ConfigRepository @Inject internal constructor(
    private val configDao: DictionaryConfigDao,
    private val categoryDao: CategoryDao,
    private val dictionaryViewDao: DictionaryViewDao,
    private val toDataCategory: CategoryEntityToDataCategory,
    private val toDictionaryView: DictionaryViewWithCategoriesToDictionaryView
) {
    private fun getConfig(dictionary: Dictionary): Flow<DictionaryConfigWithRelations?> =
        configDao.getConfigFor(dictionary.id)

    internal suspend fun getDefaultSortCategory(dictionary: InstalledDictionary): CategoryEntity? =
        categoryDao.findById(dictionary.id, Constants.DEFAULT_CATEGORY_ID)

    public fun getSortCategory(dictionary: Dictionary): Flow<DataCategory?> =
        getConfig(dictionary).map { config -> config?.category?.let { toDataCategory(it) } }

    public fun getSortCategories(dictionary: Dictionary): Flow<List<DataCategory>?> =
        categoryDao.getSortable(dictionary.id)
            .map { list -> list.map { toDataCategory(it) } }

    internal suspend fun getDefaultDictionaryView(
        dictionary: InstalledDictionary
    ): DictionaryViewWithCategories? =
        dictionaryViewDao.findById(dictionary.id, Constants.DEFAULT_DICTIONARY_VIEW_ID)

    public fun getDictionaryView(dictionary: Dictionary): Flow<DictionaryView?> =
        getConfig(dictionary).map { config -> config?.view?.let { toDictionaryView(it) } }

    public fun getDictionaryViews(dictionary: Dictionary): Flow<List<DictionaryView>?> =
        dictionaryViewDao.getAll(dictionary.id)
            .map { list -> list.map { toDictionaryView(it) } }
}
