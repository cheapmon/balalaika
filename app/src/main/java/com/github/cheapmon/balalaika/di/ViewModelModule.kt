package com.github.cheapmon.balalaika.di

import android.content.Context
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.data.storage.Storage
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {
    @Provides
    fun provideDictionaryViewModelFactory(
        context: Context,
        storage: Storage,
        repository: DictionaryRepository
    ): DictionaryViewModelFactory {
        val categoryId =
            storage.getString(context.getString(R.string.preferences_key_order), null)?.toLong()
        val dictionaryViewId =
            storage.getString(context.getString(R.string.preferences_key_view), null)?.toLong()
        return DictionaryViewModelFactory(repository, categoryId, dictionaryViewId)
    }
}