package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryView
import com.github.cheapmon.balalaika.model.InstalledDictionary
import kotlinx.coroutines.flow.Flow

internal interface ConfigRepository {
    fun getSortCategory(dictionary: InstalledDictionary): Flow<DataCategory?>

    fun getSortCategories(dictionary: InstalledDictionary): Flow<List<DataCategory>?>

    fun getDictionaryView(dictionary: InstalledDictionary): Flow<DictionaryView?>

    fun getDictionaryViews(dictionary: InstalledDictionary): Flow<List<DictionaryView>?>

    suspend fun updateConfig(
        dictionary: InstalledDictionary,
        sortBy: DataCategory,
        filterBy: DictionaryView
    )
}