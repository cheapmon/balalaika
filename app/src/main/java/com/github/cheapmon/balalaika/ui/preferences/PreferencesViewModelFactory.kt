package com.github.cheapmon.balalaika.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import javax.inject.Inject

class PreferencesViewModelFactory @Inject constructor(
    private val repository: DictionaryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PreferencesViewModel(
            repository
        ) as T
    }
}