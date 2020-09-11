package com.github.cheapmon.balalaika.data.repositories.dictionary.install

import com.github.cheapmon.balalaika.model.DownloadableDictionary

internal interface DictionaryInstaller {
    fun addDictionaryToLibrary(
        dictionary: DownloadableDictionary
    ): InstallationProgress

    fun removeDictionaryFromLibrary(
        dictionary: DownloadableDictionary
    ): InstallationProgress

    fun updateDictionary(
        dictionary: DownloadableDictionary
    ): InstallationProgress
}
