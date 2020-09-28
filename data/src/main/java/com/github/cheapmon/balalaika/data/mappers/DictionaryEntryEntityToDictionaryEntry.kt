package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.model.Bookmark
import com.github.cheapmon.balalaika.model.DictionaryEntry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DictionaryEntryEntityToDictionaryEntry @Inject constructor(
    private val dictionaryEntryDao: DictionaryEntryDao,
    private val toDataCategory: CategoryEntityToDataCategory,
    private val toProperty: PropertyEntityToProperty
) {
    suspend operator fun invoke(
        entry: DictionaryEntryEntity,
        view: DictionaryViewWithCategories
    ): DictionaryEntry = DictionaryEntry(
        id = entry.lexeme.id,
        representation = entry.lexeme.form,
        base = entry.base
            ?.let { dictionaryEntryDao.findEntryById(it.dictionaryId, it.id) }
            ?.let { this(it, view) },
        properties = entry.properties
            .groupBy { it.category }
            .mapKeys { (category, _) -> toDataCategory(category) }
            .mapValues { (_, properties) ->
                properties
                    .filter { view.categories.contains(it.category) }
                    .mapNotNull { toProperty(it.property, it.category, view, this) }
            }.toSortedMap { o1, o2 -> o1.sequence.compareTo(o2.sequence) },
        bookmark = entry.bookmark?.let { Bookmark() }
    )
}
