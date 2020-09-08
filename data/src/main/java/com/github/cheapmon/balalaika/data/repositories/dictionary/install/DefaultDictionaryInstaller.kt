package com.github.cheapmon.balalaika.data.repositories.dictionary.install

import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.model.DownloadableDictionary
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultDictionaryInstaller @Inject constructor(
    private val dao: DictionaryDao
) : DictionaryInstaller {
    override suspend fun addDictionaryToLibrary(
        dictionary: DownloadableDictionary
    ): InstallationProgress {
        TODO("Not yet implemented")
    }

    override suspend fun removeDictionaryFromLibrary(
        dictionary: DownloadableDictionary
    ): InstallationProgress {
        TODO("Not yet implemented")
    }

    override suspend fun updateDictionary(
        dictionary: DownloadableDictionary
    ): InstallationProgress {
        TODO("Not yet implemented")
    }
}
