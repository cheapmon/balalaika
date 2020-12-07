package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.model.DictionaryEntry

/** Map from a [DictionaryEntryEntity] to a [DictionaryEntry] */
internal interface DictionaryEntryEntityToDictionaryEntryMapper {
    /** @suppress */
    suspend operator fun invoke(
        entry: DictionaryEntryEntity,
        view: DictionaryViewWithCategories
    ): DictionaryEntry
}
