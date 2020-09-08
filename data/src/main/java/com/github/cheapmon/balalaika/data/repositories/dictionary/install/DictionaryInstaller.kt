package com.github.cheapmon.balalaika.data.repositories.dictionary.install

import com.github.cheapmon.balalaika.model.DownloadableDictionary

internal interface DictionaryInstaller {
    suspend fun addDictionaryToLibrary(
        dictionary: DownloadableDictionary
    ): InstallationProgress

    suspend fun removeDictionaryFromLibrary(
        dictionary: DownloadableDictionary
    ): InstallationProgress

    suspend fun updateDictionary(
        dictionary: DownloadableDictionary
    ): InstallationProgress
}
