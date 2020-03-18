package com.github.cheapmon.balalaika.util

import android.content.Context
import com.github.cheapmon.balalaika.data.DB
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.data.repositories.HistoryRepository
import com.github.cheapmon.balalaika.data.repositories.SearchRepository
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryViewModelFactory
import com.github.cheapmon.balalaika.ui.history.HistoryViewModelFactory
import com.github.cheapmon.balalaika.ui.search.SearchViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
object InjectorUtil {
    private fun provideDictionaryRepository(context: Context): DictionaryRepository {
        val db = DB.getInstance(context.applicationContext)
        return DictionaryRepository.getInstance(
            db.categories(),
            db.lexemes(),
            db.properties(),
            db.dictionaryViews()
        )
    }

    private fun provideSearchRepository(context: Context): SearchRepository {
        val db = DB.getInstance(context.applicationContext)
        return SearchRepository.getInstance(db.lexemes(), db.properties())
    }

    private fun provideHistoryRepository(context: Context): HistoryRepository {
        val db = DB.getInstance(context.applicationContext)
        return HistoryRepository(db.historyEntries())
    }

    fun provideDictionaryViewModelFactory(context: Context): DictionaryViewModelFactory {
        val repository = provideDictionaryRepository(context)
        return DictionaryViewModelFactory(repository)
    }

    fun provideSearchViewModelFactory(context: Context): SearchViewModelFactory {
        val repository = provideSearchRepository(context)
        return SearchViewModelFactory(repository)
    }

    fun provideHistoryViewModelFactory(context: Context): HistoryViewModelFactory {
        val repository = provideHistoryRepository(context)
        return HistoryViewModelFactory(repository)
    }
}