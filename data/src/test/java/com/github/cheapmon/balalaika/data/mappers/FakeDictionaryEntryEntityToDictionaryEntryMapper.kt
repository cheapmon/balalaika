package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.model.DictionaryEntry
import java.util.*

internal class FakeDictionaryEntryEntityToDictionaryEntryMapper :
    DictionaryEntryEntityToDictionaryEntryMapper {
    override suspend fun invoke(
        entry: DictionaryEntryEntity,
        view: DictionaryViewWithCategories
    ): DictionaryEntry {
        val base = entry.base?.let {
            DictionaryEntry(
                id = it.id,
                representation = it.form,
                base = null,
                properties = TreeMap(),
                bookmark = null
            )
        }
        return DictionaryEntry(
            id = entry.lexeme.id,
            representation = entry.lexeme.form,
            base = base,
            properties = TreeMap(),
            bookmark = null
        )
    }
}