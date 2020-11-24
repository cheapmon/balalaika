package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.model.DictionaryEntry

internal interface DictionaryEntryEntityToDictionaryEntryMapper {
    suspend operator fun invoke(
        entry: DictionaryEntryEntity,
        view: DictionaryViewWithCategories
    ): DictionaryEntry
}
