package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryView
import com.github.cheapmon.balalaika.model.InstalledDictionary
import kotlinx.coroutines.flow.Flow

public interface ConfigRepository {
    public fun getSortCategory(dictionary: InstalledDictionary): Flow<DataCategory?>

    public fun getSortCategories(dictionary: InstalledDictionary): Flow<List<DataCategory>?>

    public fun getDictionaryView(dictionary: InstalledDictionary): Flow<DictionaryView?>

    public fun getDictionaryViews(dictionary: InstalledDictionary): Flow<List<DictionaryView>?>

    public suspend fun updateConfig(
        dictionary: InstalledDictionary,
        sortBy: DataCategory,
        filterBy: DictionaryView
    )
}
