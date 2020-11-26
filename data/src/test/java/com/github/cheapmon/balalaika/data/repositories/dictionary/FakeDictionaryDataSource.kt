package com.github.cheapmon.balalaika.data.repositories.dictionary

import com.github.cheapmon.balalaika.model.Dictionary

internal class FakeDictionaryDataSource(
    private val dictionaries: List<Dictionary>
) : DictionaryDataSource {
    override suspend fun getDictionaryList(): List<Dictionary> = dictionaries

    override suspend fun hasDictionary(id: String, version: Int): Boolean {
        return dictionaries.find { it.id == id && it.version == version } != null
    }

    override suspend fun getDictionaryContents(id: String, version: Int): ByteArray {
        return ByteArray(0)
    }
}
