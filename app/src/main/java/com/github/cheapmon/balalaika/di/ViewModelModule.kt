package com.github.cheapmon.balalaika.di

import android.content.Context
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.data.repositories.HistoryRepository
import com.github.cheapmon.balalaika.data.repositories.SearchRepository
import com.github.cheapmon.balalaika.data.storage.Storage
import com.github.cheapmon.balalaika.ui.bookmarks.BookmarksViewModel
import com.github.cheapmon.balalaika.ui.bookmarks.BookmarksViewModelFactory
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryViewModel
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryViewModelFactory
import com.github.cheapmon.balalaika.ui.history.HistoryViewModel
import com.github.cheapmon.balalaika.ui.history.HistoryViewModelFactory
import com.github.cheapmon.balalaika.ui.preferences.PreferencesViewModel
import com.github.cheapmon.balalaika.ui.preferences.PreferencesViewModelFactory
import com.github.cheapmon.balalaika.ui.search.SearchViewModel
import com.github.cheapmon.balalaika.ui.search.SearchViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {
    @ActivityScope
    @Provides
    fun provideDictionaryViewModelFactory(
        context: Context,
        storage: Storage,
        repository: DictionaryRepository
    ): DictionaryViewModel {
        val comparatorName =
            storage.getString(context.getString(R.string.preferences_key_order), null)
        val dictionaryViewId =
            storage.getString(context.getString(R.string.preferences_key_view), null)?.toLong()
        return DictionaryViewModelFactory(repository, comparatorName, dictionaryViewId)
            .create(DictionaryViewModel::class.java)
    }

    @ActivityScope
    @Provides
    fun provideBookmarksViewModelFactory(
        repository: DictionaryRepository
    ): BookmarksViewModel {
        return BookmarksViewModelFactory(repository)
            .create(BookmarksViewModel::class.java)
    }

    @ActivityScope
    @Provides
    fun provideSearchViewModelFactory(
        searchRepository: SearchRepository,
        historyRepository: HistoryRepository
    ): SearchViewModel {
        return SearchViewModelFactory(searchRepository, historyRepository)
            .create(SearchViewModel::class.java)
    }

    @ActivityScope
    @Provides
    fun provideHistoryViewModelFactory(
        repository: HistoryRepository
    ): HistoryViewModel {
        return HistoryViewModelFactory(repository)
            .create(HistoryViewModel::class.java)
    }

    @ActivityScope
    @Provides
    fun providePreferencesViewModelFactory(
        repository: DictionaryRepository
    ): PreferencesViewModel {
        return PreferencesViewModelFactory(repository)
            .create(PreferencesViewModel::class.java)
    }
}