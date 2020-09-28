package com.github.cheapmon.balalaika.data.repositories.dictionary.install

import com.github.cheapmon.balalaika.model.InstalledDictionary
import com.github.cheapmon.balalaika.model.SimpleDictionary

internal interface DictionaryInstaller {
    fun addDictionaryToLibrary(
        dictionary: SimpleDictionary
    ): InstallationProgress

    fun removeDictionaryFromLibrary(
        dictionary: InstalledDictionary
    ): InstallationProgress

    fun updateDictionary(
        dictionary: InstalledDictionary
    ): InstallationProgress
}
