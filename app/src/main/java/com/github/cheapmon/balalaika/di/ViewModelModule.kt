package com.github.cheapmon.balalaika.di

import android.content.Context
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.data.repositories.HistoryRepository
import com.github.cheapmon.balalaika.data.repositories.SearchRepository
import com.github.cheapmon.balalaika.data.storage.Storage
import com.github.cheapmon.balalaika.ui.bookmarks.BookmarksViewModel
import com.github.cheapmon.balalaika.ui.bookmarks.BookmarksViewModelFactory
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryViewModelFactory
import com.github.cheapmon.balalaika.ui.history.HistoryViewModel
import com.github.cheapmon.balalaika.ui.history.HistoryViewModelFactory
import com.github.cheapmon.balalaika.ui.preferences.PreferencesViewModel
import com.github.cheapmon.balalaika.ui.preferences.PreferencesViewModelFactory
import com.github.cheapmon.balalaika.ui.search.SearchViewModel
import com.github.cheapmon.balalaika.ui.search.SearchViewModelFactory
import dagger.Binds
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
        val comparatorName =
            storage.getString(context.getString(R.string.preferences_key_order), null)
        val dictionaryViewId =
            storage.getString(context.getString(R.string.preferences_key_view), null)?.toLong()
        return DictionaryViewModelFactory(repository, comparatorName, dictionaryViewId)
    }
}