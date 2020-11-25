package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.db.category.CategoryDao
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfig
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfigDao
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfigWithRelations
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewDao
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.data.mappers.CategoryEntityToDataCategory
import com.github.cheapmon.balalaika.data.mappers.DictionaryViewWithCategoriesToDictionaryView
import com.github.cheapmon.balalaika.data.util.Constants
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryView
import com.github.cheapmon.balalaika.model.InstalledDictionary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class DefaultConfigRepository @Inject internal constructor(
    private val configDao: DictionaryConfigDao,
    private val categoryDao: CategoryDao,
    private val dictionaryViewDao: DictionaryViewDao,
    private val toDataCategory: CategoryEntityToDataCategory,
    private val toDictionaryView: DictionaryViewWithCategoriesToDictionaryView
) : ConfigRepository {
    private fun getConfig(dictionary: InstalledDictionary): Flow<DictionaryConfigWithRelations?> =
        configDao.getConfigFor(dictionary.id)

    private suspend fun getDefaultSortCategory(dictionary: InstalledDictionary): CategoryEntity? =
        categoryDao.findById(dictionary.id, Constants.DEFAULT_CATEGORY_ID)

    public override fun getSortCategory(dictionary: InstalledDictionary): Flow<DataCategory?> =
        getConfig(dictionary).map { config ->
            config?.category?.let { toDataCategory(it) }
                ?: getDefaultSortCategory(dictionary)?.let { toDataCategory(it) }
        }

    public override fun getSortCategories(dictionary: InstalledDictionary): Flow<List<DataCategory>?> =
        categoryDao.getSortable(dictionary.id)
            .map { list -> list.map { toDataCategory(it) } }

    private suspend fun getDefaultDictionaryView(
        dictionary: InstalledDictionary
    ): DictionaryViewWithCategories? =
        dictionaryViewDao.findById(dictionary.id, Constants.DEFAULT_DICTIONARY_VIEW_ID)

    public override fun getDictionaryView(dictionary: InstalledDictionary): Flow<DictionaryView?> =
        getConfig(dictionary).map { config ->
            config?.view?.let { toDictionaryView(it) }
                ?: getDefaultDictionaryView(dictionary)?.let { toDictionaryView(it) }
        }

    public override fun getDictionaryViews(dictionary: InstalledDictionary): Flow<List<DictionaryView>?> =
        dictionaryViewDao.getAll(dictionary.id)
            .map { list -> list.map { toDictionaryView(it) } }

    public override suspend fun updateConfig(
        dictionary: InstalledDictionary,
        sortBy: DataCategory,
        filterBy: DictionaryView
    ) {
        configDao.update(DictionaryConfig(dictionary.id, sortBy.id, filterBy.id))
    }
}
