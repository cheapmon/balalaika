package com.github.cheapmon.balalaika.data.prefs

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalCoroutinesApi::class)
internal class FakePreferenceStorage : PreferenceStorage {
    private val dictionary: MutableStateFlow<String?> = MutableStateFlow(null)

    override val openDictionary: Flow<String?> = dictionary

    override suspend fun setOpenDictionary(dictionaryId: String?) {
        dictionary.value = dictionaryId
    }
}
