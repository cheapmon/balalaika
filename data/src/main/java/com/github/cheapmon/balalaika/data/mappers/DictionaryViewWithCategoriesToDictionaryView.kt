package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.model.DictionaryView
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Map from a [DictionaryViewWithCategories] to a [DictionaryView]
 *
 * @property toDataCategory Mapper for data categories
 */
@Singleton
internal class DictionaryViewWithCategoriesToDictionaryView @Inject constructor(
    private val toDataCategory: CategoryEntityToDataCategory
) :
    Mapper<DictionaryViewWithCategories, DictionaryView> {
    /** @suppress */
    override suspend fun invoke(from: DictionaryViewWithCategories): DictionaryView =
        DictionaryView(
            id = from.dictionaryView.id,
            name = from.dictionaryView.name,
            categories = from.categories.map { toDataCategory(it) }
        )
}
