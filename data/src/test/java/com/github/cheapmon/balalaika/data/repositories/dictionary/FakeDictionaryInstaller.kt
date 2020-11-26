package com.github.cheapmon.balalaika.data.repositories.dictionary

import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.repositories.dictionary.install.DictionaryInstaller
import com.github.cheapmon.balalaika.data.repositories.dictionary.install.InstallationMessage
import com.github.cheapmon.balalaika.data.repositories.dictionary.install.InstallationProgress
import com.github.cheapmon.balalaika.data.result.tryProgress
import com.github.cheapmon.balalaika.model.InstalledDictionary
import com.github.cheapmon.balalaika.model.SimpleDictionary
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn

internal class FakeDictionaryInstaller(
    private val dispatcher: CoroutineDispatcher,
    private val dictionaryDao: DictionaryDao
) : DictionaryInstaller {
    override fun addDictionaryToLibrary(dictionary: SimpleDictionary): InstallationProgress {
        return tryProgress<Unit, InstallationMessage> {
            dictionaryDao.insertAll(
                listOf(
                    DictionaryEntity(
                        id = dictionary.id,
                        version = dictionary.version,
                        name = dictionary.name,
                        summary = dictionary.summary,
                        authors = dictionary.authors,
                        additionalInfo = dictionary.additionalInfo
                    )
                )
            )
        }.flowOn(dispatcher)
    }

    override fun removeDictionaryFromLibrary(dictionary: InstalledDictionary): InstallationProgress {
        return tryProgress<Unit, InstallationMessage> {
            dictionaryDao.remove(dictionary.id)
        }.flowOn(dispatcher)
    }

    override fun updateDictionary(dictionary: InstalledDictionary): InstallationProgress {
        return tryProgress<Unit, InstallationMessage> { }.flowOn(dispatcher)
    }
}
